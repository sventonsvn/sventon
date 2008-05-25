/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.service;

import de.berlios.sventon.SventonException;
import de.berlios.sventon.appl.InstanceConfiguration;
import de.berlios.sventon.colorer.Colorer;
import de.berlios.sventon.diff.DiffException;
import de.berlios.sventon.model.*;
import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.repository.cache.objectcache.ObjectCache;
import de.berlios.sventon.repository.export.ExportDirectory;
import de.berlios.sventon.web.command.DiffCommand;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Service class for accessing the subversion repository.
 *
 * @author jesper@users.berlios.de
 */
public interface RepositoryService {

  /**
   * Gets revision details for a specific revision number.
   * If caching is enabled in the {@link InstanceConfiguration}, a cached revision will be returned.
   *
   * @param instanceName The instance name
   * @param repository   The repository
   * @param revision     Revision number
   * @return The log entry
   * @throws SVNException     if subversion error
   * @throws SventonException if a sventon specific error occurs
   */
  SVNLogEntry getRevision(final String instanceName, final SVNRepository repository, final long revision)
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
   * If caching is enabled in the {@link InstanceConfiguration}, cached revisions will be returned.
   *
   * @param instanceName The instance name
   * @param repository   The repository
   * @param fromRevision From revision
   * @param toRevision   To revision
   * @param path         The repository path
   * @param limit        Revision limit
   * @return The log entries
   * @throws SVNException     if subversion error
   * @throws SventonException if a sventon specific error occurs
   */
  List<SVNLogEntry> getRevisions(final String instanceName, final SVNRepository repository,
                                 final long fromRevision, final long toRevision, final String path,
                                 final long limit) throws SVNException, SventonException;

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
   * Gets a file from the repository.
   *
   * @param repository The repository
   * @param path       Target to get
   * @param revision   The revision
   * @param output     Output stream to write contents to
   * @param properties The map to be populated with the file's properties
   * @throws SVNException if a subversion error occur
   */
  void getFile(final SVNRepository repository, final String path, final long revision, final OutputStream output,
               final Map properties) throws SVNException;

  /**
   * Gets a file's properties from the repository.
   *
   * @param repository The repository
   * @param path       Target of target to get properties for
   * @param revision   The revision
   * @return Map populated with the file's properties
   * @throws SVNException if a subversion error occur
   */
  Map getFileProperties(final SVNRepository repository, final String path, final long revision) throws SVNException;

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
   * @return Map containing path
   * @throws SVNException if a subversion error occur
   */
  Map<String, SVNLock> getLocks(final SVNRepository repository, final String startPath) throws SVNException;

  /**
   * @param repository The repository
   * @param path       The entry path
   * @param revision   The revision
   * @param properties The entry properties
   * @return List of entries
   * @throws SVNException if a subversion error occur
   */
  List<RepositoryEntry> list(final SVNRepository repository, final String path, final long revision,
                             final Map properties) throws SVNException;

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
   * Gets a thumbnail image from given full size image url.
   *
   * @param repository       The repository
   * @param objectCache      Cache instance
   * @param path             The entry path
   * @param revision         The entry revision
   * @param fullSizeImageUrl Image URL
   * @param imageFormatName  Format name
   * @param maxThumbnailSize Size
   * @param out              Destination output stream
   * @return null
   * @throws SVNException if a subversion error occur
   */
  ImageMetadata getThumbnailImage(final SVNRepository repository, final ObjectCache objectCache, final String path,
                                  final long revision, final URL fullSizeImageUrl, final String imageFormatName,
                                  final int maxThumbnailSize, final OutputStream out) throws SVNException;

  /**
   * Creates a side-by-side diff.
   *
   * @param repository    The repository.
   * @param diffCommand   DiffCommand.
   * @param pegRevision   Peg revision, or {@link SVNRevision#UNDEFINED} of n/a.
   * @param charset       The charset to use.
   * @param configuration The instance configuration. @return Ordered list of diffed rows.
   * @throws SVNException  if a subversion error occur
   * @throws DiffException if unable to produce diff.
   */
  List<SideBySideDiffRow> diffSideBySide(final SVNRepository repository, final DiffCommand diffCommand,
                                         final SVNRevision pegRevision, final String charset, final InstanceConfiguration configuration)
      throws SVNException, DiffException;

  /**
   * Creates a unified diff.
   *
   * @param repository    The repository.
   * @param diffCommand   DiffCommand.
   * @param pegRevision   Peg revision, or {@link SVNRevision#UNDEFINED} of n/a.
   * @param charset       The charset to use.
   * @param configuration The instance configuration. @return The unified diff as a string.
   * @throws SVNException  if a subversion error occur
   * @throws DiffException if unable to produce diff.
   */
  String diffUnified(final SVNRepository repository, final DiffCommand diffCommand, final SVNRevision pegRevision, final String charset,
                     final InstanceConfiguration configuration)
      throws SVNException, DiffException;

  /**
   * Creates an inline diff.
   *
   * @param repository    The repository.
   * @param diffCommand   DiffCommand.
   * @param pegRevision   Peg revision, or {@link SVNRevision#UNDEFINED} of n/a.
   * @param charset       The charset to use.
   * @param configuration The instance configuration. @return The inline diff.
   * @throws SVNException  if a subversion error occur
   * @throws DiffException if unable to produce diff.
   */
  List<InlineDiffRow> diffInline(final SVNRepository repository, final DiffCommand diffCommand, final SVNRevision pegRevision, final String charset,
                                 final InstanceConfiguration configuration) throws SVNException, DiffException;

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
}
