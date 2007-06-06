/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.service;

import de.berlios.sventon.config.InstanceConfiguration;
import de.berlios.sventon.diff.DiffException;
import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.repository.cache.CacheException;
import de.berlios.sventon.repository.cache.objectcache.ObjectCache;
import de.berlios.sventon.repository.export.ExportDirectory;
import de.berlios.sventon.web.command.DiffCommand;
import de.berlios.sventon.web.model.ImageMetadata;
import de.berlios.sventon.web.model.RawTextFile;
import de.berlios.sventon.web.model.SideBySideDiffRow;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
   * @throws SVNException   if subversion error
   * @throws CacheException if unable to get cached revision
   */
  SVNLogEntry getRevision(final String instanceName, final SVNRepository repository, final long revision)
      throws SVNException, CacheException;

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
   * @throws SVNException   if subversion error
   * @throws CacheException if unable to get cached revision
   */
  List<SVNLogEntry> getRevisions(final String instanceName, final SVNRepository repository,
                                 final long fromRevision, final long toRevision, final String path,
                                 final long limit) throws SVNException, CacheException;

  /**
   * Exports given list of target entries to the given destination export directory.
   *
   * @param repository      The repository
   * @param targets         Targets to export.
   * @param revision        Revision to export
   * @param exportDirectory Destination directory
   * @throws SVNException if a subversion error occur
   */
  void export(final SVNRepository repository, final List<String> targets, final long revision,
              final ExportDirectory exportDirectory) throws SVNException;

  /**
   * Gets a file from the repository as a raw text file.
   *
   * @param repository The repository
   * @param path       Path
   * @param revision   Revision
   * @param charset    Charset encoding to use
   * @return The text file instance
   * @throws SVNException                 if a subversion error occur
   * @throws UnsupportedEncodingException if given charset encoding is invalid
   */
  RawTextFile getTextFile(final SVNRepository repository, final String path, final long revision, final String charset)
      throws SVNException, UnsupportedEncodingException;

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
   * Gets the latest repository revisions.
   *
   * @param instanceName  The instance name
   * @param repository    The repository
   * @param revisionCount Number of revisions to fetch
   * @return The revisions.
   * @throws SVNException   if a subversion error occur
   * @throws CacheException if unable to get cached revision
   */
  List<SVNLogEntry> getLatestRevisions(final String instanceName, final SVNRepository repository,
                                       final long revisionCount) throws SVNException, CacheException;

  /**
   * Gets the latest repository revisions.
   *
   * @param instanceName  The instance name
   * @param repository    The repository
   * @param path          The repository path
   * @param revisionCount Number of revisions to fetch
   * @return The revisions.
   * @throws SVNException   if a subversion error occur
   * @throws CacheException if unable to get cached revision
   */
  List<SVNLogEntry> getLatestRevisions(final String instanceName, final String path, final SVNRepository repository,
                                       final long revisionCount) throws SVNException, CacheException;

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
   * @return Entry or <tt>null</tt> if no entry exists at given path and revision
   * @throws SVNException if a subversion error occur
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

  ImageMetadata getThumbnailImage(final SVNRepository repository, final ObjectCache objectCache, final String path,
                                  final long revision, final URL fullSizeImageUrl, final String imageFormatName,
                                  final int maxThumbnailSize, final OutputStream out) throws SVNException;

  /**
   * @param repository    The repository
   * @param diffCommand   Diffcommand.
   * @param charset       The charset to use.
   * @param configuration The instance configuration.
   * @return Ordered list of diffed rows.
   * @throws SVNException  if a subversion error occur
   * @throws DiffException if unable to produce diff.
   */
  List<SideBySideDiffRow> diffSideBySide(final SVNRepository repository, final DiffCommand diffCommand,
                                         final String charset, final InstanceConfiguration configuration)
      throws SVNException, DiffException;

  /**
   * @param repository    The repository
   * @param diffCommand   Diffcommand
   * @param charset       The charset to use.
   * @param configuration The instance configuration.
   * @return The unified diff as a string.
   * @throws SVNException  if a subversion error occur
   * @throws DiffException if unable to produce diff.
   */
  String diffUnified(final SVNRepository repository, final DiffCommand diffCommand, final String charset,
                     final InstanceConfiguration configuration)
      throws SVNException, DiffException;

}
