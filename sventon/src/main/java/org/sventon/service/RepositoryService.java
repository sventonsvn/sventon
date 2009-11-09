/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.service;

import org.sventon.SventonException;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.colorer.Colorer;
import org.sventon.diff.DiffException;
import org.sventon.export.ExportDirectory;
import org.sventon.model.*;
import org.sventon.web.command.DiffCommand;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNDiffStatus;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.IOException;
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
   * @param repository     The repository
   * @param revision       Revision number
   * @return The log entry
   * @throws SVNException                 if subversion error
   * @throws org.sventon.SventonException if a sventon specific error occurs
   */
  SVNLogEntry getRevision(final RepositoryName repositoryName, final SVNRepository repository, final long revision)
      throws SVNException, SventonException;

  /**
   * Gets revision details for given revision interval.
   * This method will do a deep log fetch from the repository.
   *
   * @param repository   The repository
   * @param fromRevision From revision
   * @param toRevision   To revision
   * @return The log entries
   * @throws SVNException if subversion error
   */
  List<SVNLogEntry> getRevisionsFromRepository(final SVNRepository repository, final long fromRevision, final long toRevision)
      throws SVNException;

  /**
   * Gets revision details for given revision interval and a specific path with limit.
   * If caching is enabled in the {@link org.sventon.appl.RepositoryConfiguration}, cached revisions will be returned.
   *
   * @param repositoryName Repository name.
   * @param repository     The repository
   * @param fromRevision   From revision
   * @param toRevision     To revision
   * @param path           The repository path
   * @param limit          Revision limit
   * @param stopOnCopy     Stop on copy
   * @return The log entries
   * @throws SVNException     if subversion error
   * @throws SventonException if a sventon specific error occurs
   */
  List<SVNLogEntry> getRevisions(final RepositoryName repositoryName, final SVNRepository repository,
                                 final long fromRevision, final long toRevision, final String path,
                                 final long limit, final boolean stopOnCopy) throws SVNException, SventonException;

  /**
   * Exports given list of target entries to the given destination export directory.
   *
   * @param repository      The repository
   * @param targets         Targets to export.
   * @param pegRevision     Peg revision
   * @param exportDirectory Destination directory
   * @throws SVNException if a subversion error occur
   */
  void export(final SVNRepository repository, final List<SVNFileRevision> targets, final long pegRevision,
              final ExportDirectory exportDirectory) throws SVNException;

  /**
   * Gets a file from the repository as a raw text file.
   *
   * @param repository The repository
   * @param path       Path
   * @param revision   Revision
   * @param charset    Charset encoding to use
   * @return The text file instance
   * @throws SVNException if a subversion error occur
   * @throws IOException  if given charset encoding is invalid
   */
  TextFile getTextFile(final SVNRepository repository, final String path, final long revision, final String charset)
      throws SVNException, IOException;

  /**
   * Gets a file from the repository.
   *
   * @param repository The repository
   * @param path       Target to get
   * @param revision   The revision
   * @param output     Output stream to write contents to
   * @throws SVNException if a subversion error occur
   */
  void getFile(final SVNRepository repository, final String path, final long revision, final OutputStream output)
      throws SVNException;

  /**
   * Gets a file's properties from the repository.
   *
   * @param repository The repository
   * @param path       Target of target to get properties for
   * @param revision   The revision
   * @return The file's properties
   * @throws SVNException if a subversion error occur
   */
  SVNProperties getFileProperties(final SVNRepository repository, final String path, final long revision) throws SVNException;

  /**
   * Checks whether given target file is a text file, by inspecting it's mime-type property.
   *
   * @param repository The repository
   * @param path       Target of target to get properties for
   * @param revision   The revision
   * @return <code>true</code> if file is a text file, <code>false</code> if not.
   * @throws SVNException if a subversion error occur
   */
  boolean isTextFile(final SVNRepository repository, final String path, final long revision) throws SVNException;

  /**
   * Gets a file's checksum.
   *
   * @param repository The repository
   * @param path       Target of target to get properties for
   * @param revision   The revision
   * @return The file's checksum
   * @throws SVNException if a subversion error occur
   */
  String getFileChecksum(final SVNRepository repository, final String path, final long revision) throws SVNException;

  /**
   * Gets the latest (HEAD) repository revision.
   *
   * @param repository The repository
   * @return The HEAD revision.
   * @throws SVNException if a subversion error occur
   */
  long getLatestRevision(final SVNRepository repository) throws SVNException;

  /**
   * Gets the node type for given path (with or without leaf).
   *
   * @param repository The repository
   * @param path       The path, with or without leaf.
   * @param revision   The revision
   * @return The node kind
   * @throws SVNException if a subversion error occur
   */
  SVNNodeKind getNodeKind(final SVNRepository repository, final String path, final long revision) throws SVNException;

  /**
   * Gets the repository locks recursively, starting from given path.
   *
   * @param repository The repository
   * @param startPath  The start path. If <code>null</code> locks will be gotten from root.
   * @return Map containing path and locks.
   * @throws SVNException if a subversion error occur
   */
  Map<String, SVNLock> getLocks(final SVNRepository repository, final String startPath) ;

  /**
   * @param repository The repository
   * @param path       The entry path
   * @param revision   The revision
   * @param properties The entry properties
   * @return List of entries
   * @throws SVNException if a subversion error occur
   */
  List<RepositoryEntry> list(final SVNRepository repository, final String path, final long revision,
                             final SVNProperties properties) throws SVNException;

  /**
   * Gets entry info from the subversion repository.
   *
   * @param repository The repository
   * @param path       The entry path
   * @param revision   The entry revision
   * @return Entry
   * @throws SVNException if a subversion error occur. If the SVNErrorMessage SVNErrorCode is set to ENTRY_NOT_FOUND,
   *                      no entry exists at given path and revision.
   */
  RepositoryEntry getEntryInfo(final SVNRepository repository, final String path, final long revision) throws SVNException;

  /**
   * Gets the revisions for a specific entry.
   *
   * @param repository The repository
   * @param path       The entry path
   * @param revision   The entry revision
   * @return List of file revisions
   * @throws SVNException if a subversion error occur
   */
  List<SVNFileRevision> getFileRevisions(final SVNRepository repository, final String path, final long revision) throws SVNException;

  /**
   * Creates a side-by-side diff.
   *
   * @param repository    The repository.
   * @param command       DiffCommand.
   * @param pegRevision   Peg revision, or {@link SVNRevision#UNDEFINED} of n/a.
   * @param charset       The charset to use.
   * @param configuration The repository configuration. @return Ordered list of diffed rows.
   * @return List of diff rows.
   * @throws SVNException  if a subversion error occur
   * @throws DiffException if unable to produce diff.
   */
  List<SideBySideDiffRow> diffSideBySide(final SVNRepository repository, final DiffCommand command,
                                         final SVNRevision pegRevision, final String charset, final RepositoryConfiguration configuration)
      throws SVNException, DiffException;

  /**
   * Creates a unified diff.
   *
   * @param repository    The repository.
   * @param command       DiffCommand.
   * @param pegRevision   Peg revision, or {@link SVNRevision#UNDEFINED} of n/a.
   * @param charset       The charset to use.
   * @param configuration The repository configuration. @return The unified diff as a string.
   * @return Diff result.
   * @throws SVNException  if a subversion error occur
   * @throws DiffException if unable to produce diff.
   */
  String diffUnified(final SVNRepository repository, final DiffCommand command, final SVNRevision pegRevision, final String charset,
                     final RepositoryConfiguration configuration)
      throws SVNException, DiffException;

  /**
   * Creates an inline diff.
   *
   * @param repository    The repository.
   * @param command       DiffCommand.
   * @param pegRevision   Peg revision, or {@link SVNRevision#UNDEFINED} of n/a.
   * @param charset       The charset to use.
   * @param configuration The repository configuration. @return The inline diff.
   * @return List of diff rows.
   * @throws SVNException  if a subversion error occur
   * @throws DiffException if unable to produce diff.
   */
  List<InlineDiffRow> diffInline(final SVNRepository repository, final DiffCommand command, final SVNRevision pegRevision, final String charset,
                                 final RepositoryConfiguration configuration) throws SVNException, DiffException;

  /**
   * Creates a path diff.
   *
   * @param repository    The repository.
   * @param command       DiffCommand.
   * @param configuration The repository configuration. @return The inline diff.
   * @return List of diff status.
   * @throws SVNException if a subversion error occur
   */
  List<SVNDiffStatus> diffPaths(final SVNRepository repository, final DiffCommand command,
                                final RepositoryConfiguration configuration) throws SVNException;

  /**
   * Blame (annotates) the given file.
   *
   * @param repository The repository
   * @param path       The entry path
   * @param revision   The entry revision
   * @param charset    Charset encoding to use
   * @param colorer    Colorer instance.
   * @return List of BlameLines
   * @throws SVNException if a subversion error occur
   */
  AnnotatedTextFile blame(final SVNRepository repository, final String path, final long revision, final String charset,
                          final Colorer colorer) throws SVNException;

  /**
   * Gets the path properties.
   *
   * @param repository The repository
   * @param path       The entry path
   * @param revision   The entry revision
   * @return Properties
   * @throws SVNException if a subversion error occur
   */
  SVNProperties getPathProperties(final SVNRepository repository, final String path, final long revision)
      throws SVNException;

  /**
   * Gets the node kind for given to/from entries.
   *
   * @param repository The repository
   * @param command    DiffCommand.
   * @return Node kind
   * @throws SVNException  if a subversion error occur
   * @throws DiffException Thrown if from/to entries are of different node kinds (eg. trying to diff a file and a dir)
   *                       of if one of the given entries does not exist in given revision.
   */
  SVNNodeKind getNodeKindForDiff(final SVNRepository repository, final DiffCommand command)
      throws SVNException, DiffException;
}
