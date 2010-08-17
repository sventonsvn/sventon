/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.service;

import org.sventon.SVNConnection;
import org.sventon.SventonException;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.colorer.Colorer;
import org.sventon.diff.DiffException;
import org.sventon.export.ExportDirectory;
import org.sventon.model.*;
import org.sventon.web.command.DiffCommand;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Service class for accessing the subversion repository.
 *
 * @author jesper@sventon.org
 */
public interface RepositoryService {

  /**
   * Gets revision details for a specific revision number.
   * If caching is enabled in the {@link org.sventon.appl.RepositoryConfiguration}, a cached revision will be returned.
   *
   * @param repositoryName Repository name.
   * @param connection     The repository
   * @param revision       Revision number
   * @return The log entry
   * @throws SventonException if a sventon specific error occurs
   */
  LogEntry getLogEntry(final RepositoryName repositoryName, final SVNConnection connection, final long revision)
      throws SventonException;

  /**
   * Gets revision details for given revision interval.
   * This method will do a deep log fetch from the repository.
   *
   * @param connection   The repository
   * @param fromRevision From revision
   * @param toRevision   To revision
   * @return The log entries
   * @throws SventonException if subversion error
   */
  List<LogEntry> getLogEntriesFromRepositoryRoot(final SVNConnection connection, final long fromRevision, final long toRevision)
      throws SventonException;

  /**
   * Gets revision details for given revision interval and a specific path with limit.
   * If caching is enabled in the {@link org.sventon.appl.RepositoryConfiguration}, cached revisions will be returned.
   *
   * @param repositoryName      Repository name.
   * @param connection          The repository
   * @param fromRevision        From revision
   * @param toRevision          To revision
   * @param path                The repository path
   * @param limit               Revision limit
   * @param stopOnCopy          Stop on copy
   * @param includeChangedPaths If <code>true</code>, all changed paths will be included in the resulting objects.
   * @return The log entries
   * @throws SventonException if a sventon specific error occurs
   */
  List<LogEntry> getLogEntries(final RepositoryName repositoryName, final SVNConnection connection,
                               final long fromRevision, final long toRevision, final String path,
                               final long limit, final boolean stopOnCopy, boolean includeChangedPaths)
      throws SventonException;

  /**
   * Exports given list of target entries to the given destination export directory.
   *
   * @param connection      The repository
   * @param targets         Targets to export.
   * @param pegRevision     Peg revision
   * @param exportDirectory Destination directory
   * @throws SventonException if a subversion error occur
   */
  void export(final SVNConnection connection, final List<PathRevision> targets, final long pegRevision,
              final ExportDirectory exportDirectory) throws SventonException;

  /**
   * Gets a file's contents from the repository and writes it to the given output stream.
   *
   * @param connection The repository connection
   * @param path       Target to get
   * @param revision   The revision
   * @param output     Output stream to write contents to
   * @throws SventonException if a subversion error occur
   */
  void getFileContents(final SVNConnection connection, final String path, final long revision, final OutputStream output)
      throws SventonException;

  /**
   * Gets a file's properties from the repository.
   *
   * @param connection The repository connection
   * @param path       Target of target to get properties for
   * @param revision   The revision
   * @return The file's properties
   * @throws SventonException if a subversion error occur
   */
  Properties getFileProperties(final SVNConnection connection, final String path, final long revision) throws SventonException;

  /**
   * Gets a file's checksum.
   *
   * @param connection The repository connection
   * @param path       Target of target to get properties for
   * @param revision   The revision
   * @return The file's checksum
   * @throws SventonException if a subversion error occur
   */
  String getFileChecksum(final SVNConnection connection, final String path, final long revision) throws SventonException;

  /**
   * Gets the latest (HEAD) repository revision.
   *
   * @param connection The repository connection
   * @return The HEAD revision.
   * @throws SventonException if a subversion error occur
   */
  Long getLatestRevision(final SVNConnection connection) throws SventonException;

  /**
   * Gets the node kind for given path (with or without leaf).
   *
   * @param connection The repository connection
   * @param path       The path, with or without leaf.
   * @param revision   The revision
   * @return The node kind
   * @throws SventonException if a subversion error occur
   */
  DirEntry.Kind getNodeKind(final SVNConnection connection, final String path, final long revision) throws SventonException;

  /**
   * Gets the repository locks recursively, starting from given path.
   *
   * @param connection The repository connection
   * @param startPath  The start path. If <code>null</code> locks will be gotten from root.
   * @return Map containing path and locks.
   */
  Map<String, DirEntryLock> getLocks(final SVNConnection connection, final String startPath);

  /**
   * @param connection The repository connection
   * @param path       The entry path
   * @param revision   The revision
   * @return List of entries
   * @throws SventonException if a subversion error occur
   */
  DirList list(final SVNConnection connection, final String path, final long revision) throws SventonException;

  /**
   * Gets entry info from the subversion repository.
   *
   * @param connection The repository connection
   * @param path       The entry path
   * @param revision   The entry revision
   * @return Entry
   * @throws SventonException if a subversion error occur. If the SVNErrorMessage SVNErrorCode is set to ENTRY_NOT_FOUND,
   *                          no entry exists at given path and revision.
   */
  DirEntry getEntryInfo(final SVNConnection connection, final String path, final long revision) throws SventonException;

  /**
   * Gets the revisions for a specific entry.
   *
   * @param connection The repository connection
   * @param path       The entry path
   * @param revision   The entry revision
   * @return List of file revisions
   * @throws SventonException if a subversion error occur
   */
  List<FileRevision> getFileRevisions(final SVNConnection connection, final String path, final long revision) throws SventonException;

  /**
   * Creates a side-by-side diff.
   *
   * @param connection  The repository connection.
   * @param command     DiffCommand.
   * @param pegRevision Peg revision, or {@link org.sventon.model.Revision#UNDEFINED} of n/a.
   * @param charset     The charset to use.
   * @return List of diff rows.
   * @throws SventonException if a subversion error occur
   * @throws DiffException    if unable to produce diff.
   */
  List<SideBySideDiffRow> diffSideBySide(final SVNConnection connection, final DiffCommand command,
                                         final Revision pegRevision, final String charset)
      throws SventonException, DiffException;

  /**
   * Creates a unified diff.
   *
   * @param connection  The repository connection.
   * @param command     DiffCommand.
   * @param pegRevision Peg revision, or {@link org.sventon.model.Revision#UNDEFINED} of n/a.
   * @param charset     The charset to use.
   * @return Diff result.
   * @throws SventonException if a subversion error occur
   * @throws DiffException    if unable to produce diff.
   */
  String diffUnified(final SVNConnection connection, final DiffCommand command, final Revision pegRevision,
                     final String charset) throws SventonException, DiffException;

  /**
   * Creates an inline diff.
   *
   * @param connection  The repository connection.
   * @param command     DiffCommand.
   * @param pegRevision Peg revision, or {@link Revision#UNDEFINED} of n/a.
   * @param charset     The charset to use.
   * @return List of diff rows.
   * @throws SventonException if a subversion error occur
   * @throws DiffException    if unable to produce diff.
   */
  List<InlineDiffRow> diffInline(final SVNConnection connection, final DiffCommand command, final Revision pegRevision,
                                 final String charset) throws SventonException, DiffException;

  /**
   * Creates a path diff.
   *
   * @param connection    The repository connection.
   * @param command       DiffCommand.
   * @return List of diff status.
   * @throws SventonException if a subversion error occur
   */
  List<DiffStatus> diffPaths(final SVNConnection connection, final DiffCommand command) throws SventonException;

  /**
   * Blame (annotates) the given file.
   *
   * @param connection The repository connection
   * @param path       The entry path
   * @param revision   The entry revision
   * @param charset    Charset encoding to use
   * @param colorer    Colorer instance.
   * @return List of BlameLines
   * @throws SventonException if a subversion error occur
   */
  AnnotatedTextFile blame(final SVNConnection connection, final String path, final long revision, final String charset,
                          final Colorer colorer) throws SventonException;

  /**
   * Gets the node kind for given to/from entries.
   *
   * @param connection The repository connection
   * @param command    DiffCommand.
   * @return Node kind
   * @throws SventonException if a subversion error occur
   * @throws DiffException    Thrown if from/to entries are of different node kinds (eg. trying to diff a file and a dir)
   *                          of if one of the given entries does not exist in given revision.
   */
  DirEntry.Kind getNodeKindForDiff(final SVNConnection connection, final DiffCommand command)
      throws SventonException, DiffException;

  /**
   * Translates the revision and the peg revision into a number, if needed.
   * <p/>
   * Handles the logical <i>HEAD</i> revision. Also handles date based revisions,
   * by getting the closest revision number before or at the specified date stamp.
   *
   * @param revision     Revision to translate
   * @param headRevision The current HEAD revision.
   * @param connection   Repository connection.
   * @return The revision number.
   * @throws SventonException if unable to communicate with repository.
   */
  Long translateRevision(final Revision revision, final long headRevision, final SVNConnection connection) throws SventonException;

  /**
   * Gets the <code>n</code> latest revisions.
   *
   * @param repositoryName Repository name.
   * @param connection     Repository connection
   * @param revisionCount  Number of revisions to include in the result
   * @return List of the latest revisions.
   * @throws SventonException if unable to communicate with repository.
   */
  List<LogEntry> getLatestRevisions(final RepositoryName repositoryName, final SVNConnection connection,
                                    final int revisionCount) throws SventonException;
}
