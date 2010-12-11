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
package org.sventon.cache.direntrycache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.sventon.DirEntryNotFoundException;
import org.sventon.SVNConnection;
import org.sventon.SVNConnectionFactory;
import org.sventon.SventonException;
import org.sventon.appl.Application;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.cache.CacheException;
import org.sventon.cache.DirEntryCacheManager;
import org.sventon.model.ChangedPath;
import org.sventon.model.DirEntry;
import org.sventon.model.LogEntry;
import org.sventon.model.RepositoryName;
import org.sventon.repository.RepositoryChangeListener;
import org.sventon.repository.RevisionUpdate;
import org.sventon.service.RepositoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class responsible for updating one or more entry cache instances.
 *
 * @author jesper@sventon.org
 */
public final class DirEntryCacheUpdater implements RepositoryChangeListener {

  /**
   * The static logging instance.
   */
  private static final Log LOGGER = LogFactory.getLog(DirEntryCacheUpdater.class);

  /**
   * The DirEntryCacheManager instance.
   */
  private final DirEntryCacheManager cacheManager;

  /**
   * The application.
   */
  private final Application application;

  /**
   * Service.
   */
  private RepositoryService repositoryService;

  /**
   * The repository factory.
   */
  private SVNConnectionFactory connectionFactory;

  /**
   * Constructor.
   *
   * @param cacheManager The DirEntryCacheManager instance.
   * @param application  Application
   */
  public DirEntryCacheUpdater(final DirEntryCacheManager cacheManager, final Application application) {
    LOGGER.info("Starting");
    this.cacheManager = cacheManager;
    this.application = application;
  }

  /**
   * Updates the cache with the given revisions.
   *
   * @param revisionUpdate The updated revisions.
   */
  public void update(final RevisionUpdate revisionUpdate) {
    final RepositoryName repositoryName = revisionUpdate.getRepositoryName();

    LOGGER.info("Listener got [" + revisionUpdate.getRevisions().size() + "] updated revision(s) for repository: "
        + repositoryName);

    SVNConnection connection = null;
    try {
      final DirEntryCache entryCache = cacheManager.getCache(repositoryName);
      final RepositoryConfiguration configuration = application.getConfiguration(repositoryName);
      connection = connectionFactory.createConnection(repositoryName, configuration.getSVNURL(),
          configuration.getCacheCredentials());
      updateInternal(entryCache, connection, revisionUpdate);
    } catch (final Exception ex) {
      LOGGER.warn("Could not update cache instance [" + repositoryName + "]", ex);
    } finally {
      if (connection != null) {
        connection.closeSession();
      }
    }
  }

  /**
   * Internal update method. Made protected for testing reasons only.
   * <p/>
   * <table>
   * <tr><th>Type</th><th>Description</th><th>Action</th></tr>
   * <tr><td>'A'</td><td>Added</td><td>Entry is added</td></tr>
   * <tr><td>'D'</td><td>Deleted</td><td>Entry is removed</td></tr>
   * <tr><td>'M'</td><td>Modified</td><td>Entry's details are updated</td></tr>
   * <tr><td>'R'</td><td>Replaced (means that the object is first deleted, then
   * another object with the same name is added, all within a single revision)
   * </td><td>Entry's details are updated</td></tr>
   * </table>
   *
   * @param entryCache     DirEntryCache.
   * @param connection     Repository.
   * @param revisionUpdate Update
   */
  protected void updateInternal(final DirEntryCache entryCache, final SVNConnection connection,
                                final RevisionUpdate revisionUpdate) {

    final List<LogEntry> revisions = revisionUpdate.getRevisions();
    final int revisionCount = revisions.size();
    final long firstRevision = revisions.get(0).getRevision();
    final long lastRevision = revisions.get(revisionCount - 1).getRevision();

    boolean firstTime = false;
    if (revisionCount > 0 && firstRevision == 1) {
      firstTime = true;
      LOGGER.info("Starting initial cache population for: " + revisionUpdate.getRepositoryName());
    }

    // Initial population has already been performed - only apply changes for now.
    if (lastRevision > entryCache.getLatestCachedRevisionNumber()) {
      // One logEntry is one commit (or revision)
      for (final LogEntry logEntry : revisions) {
        addRevisionToCache(entryCache, connection, logEntry);
      }
      LOGGER.debug("Update completed");
    }
    if (firstTime) {
      LOGGER.info("Cache population completed for: " + revisionUpdate.getRepositoryName());
    }
  }

  private void addRevisionToCache(final DirEntryCache entryCache, final SVNConnection connection,
                                  final LogEntry logEntry) {
    try {
      final long revision = logEntry.getRevision();
      LOGGER.debug("Applying changes in revision [" + revision + "] to cache");

      final List<DirEntry> entriesToAdd = new ArrayList<DirEntry>();
      final Map<String, DirEntry.Kind> entriesToDelete = new HashMap<String, DirEntry.Kind>();

      for (final ChangedPath entryPath : logEntry.getChangedPaths()) {
        switch (entryPath.getType()) {
          case ADDED:
            LOGGER.debug("Adding entry to cache: " + entryPath.getPath());
            doEntryCacheAdd(entriesToAdd, connection, entryPath, revision);
            break;
          case DELETED:
            LOGGER.debug("Removing deleted entry from cache: " + entryPath.getPath());
            doEntryCacheDelete(entriesToDelete, connection, entryPath, revision);
            break;
          case REPLACED:
            LOGGER.debug("Replacing entry in cache: " + entryPath.getPath());
            doEntryCacheReplace(entriesToAdd, entriesToDelete, connection, entryPath, revision);
            break;
          case MODIFIED:
            LOGGER.debug("Updating modified entry in cache: " + entryPath.getPath());
            doEntryCacheModify(entriesToAdd, entriesToDelete, connection, entryPath, revision);
            break;
          default:
            throw new RuntimeException("Unknown log entry type: " + entryPath.getType() + " in rev " + logEntry.getRevision());
        }
      }
      updateAndFlushCache(entriesToAdd, entriesToDelete, revision, entryCache);
    } catch (SventonException svnex) {
      LOGGER.error("Unable to update entryCache", svnex);
    }
  }

  private void updateAndFlushCache(final List<DirEntry> entriesToAdd, final Map<String, DirEntry.Kind> entriesToDelete,
                                   final long revision, DirEntryCache entryCache) {
    entryCache.update(entriesToDelete, entriesToAdd);
    entryCache.setLatestCachedRevisionNumber(revision);

    try {
      entryCache.flush();
    } catch (final CacheException ce) {
      LOGGER.error("Unable to flush cache", ce);
    }
  }

  /**
   * Modifies an entry (file or directory) in the cache.
   *
   * @param entriesToAdd    List of entries to add.
   * @param entriesToDelete List of entries to delete.
   * @param connection      Repository
   * @param logEntryPath    The log entry path
   * @param revision        The log revision
   * @throws SventonException if subversion error occur.
   */
  private void doEntryCacheModify(final List<DirEntry> entriesToAdd,
                                  final Map<String, DirEntry.Kind> entriesToDelete,
                                  final SVNConnection connection, final ChangedPath logEntryPath,
                                  final long revision) throws SventonException {

    entriesToDelete.put(logEntryPath.getPath(), DirEntry.Kind.ANY);
    final DirEntry entry = repositoryService.getEntryInfo(connection, logEntryPath.getPath(), revision);
    entriesToAdd.add(entry);
  }

  /**
   * Replaces an entry (file or directory) in the cache.
   *
   * @param entriesToAdd    Entries
   * @param entriesToDelete List of entries to delete.
   * @param connection      Repository
   * @param logEntryPath    The log entry path
   * @param revision        The log revision
   * @throws SventonException if subversion error occur.
   */
  private void doEntryCacheReplace(final List<DirEntry> entriesToAdd,
                                   final Map<String, DirEntry.Kind> entriesToDelete,
                                   final SVNConnection connection, final ChangedPath logEntryPath,
                                   final long revision) throws SventonException {

    doEntryCacheModify(entriesToAdd, entriesToDelete, connection, logEntryPath, revision);
  }

  /**
   * Deletes an entry (file or directory) from the cache.
   *
   * @param entriesToDelete List of entries to delete.
   * @param connection      Repository
   * @param logEntryPath    The log entry path
   * @param revision        The log revision
   * @throws SventonException if subversion error occur.
   */
  private void doEntryCacheDelete(final Map<String, DirEntry.Kind> entriesToDelete,
                                  final SVNConnection connection, final ChangedPath logEntryPath,
                                  final long revision) throws SventonException {

    // Have to find out if deleted entry was a file or directory
    final long previousRevision = revision - 1;
    DirEntry deletedEntry;

    try {
      deletedEntry = repositoryService.getEntryInfo(connection, logEntryPath.getPath(), previousRevision);
      entriesToDelete.put(logEntryPath.getPath(), deletedEntry.getKind());
    } catch (DirEntryNotFoundException ex) {
      LOGGER.debug("Entry [" + logEntryPath.getPath() + "] does not exist in revision [" + previousRevision + "] - nothing to remove");
    }
  }

  /**
   * Adds an entry (file or directory) to the cache.
   *
   * @param entriesToAdd Entries
   * @param connection   Repository
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SventonException if subversion error occur.
   */
  private void doEntryCacheAdd(final List<DirEntry> entriesToAdd,
                               final SVNConnection connection, final ChangedPath logEntryPath,
                               final long revision) throws SventonException {

    // Have to find out if added entry was a file or directory
    final DirEntry entry = repositoryService.getEntryInfo(connection, logEntryPath.getPath(), revision);

    // If the entry is a directory and a copyPath exists, the entry is
    // a moved or copied directory (branch). In that case we have to recursively
    // add the entry. If entry is a directory but does not have a copyPath
    // the contents will be added one by one as single entriesToAdd.
    if (entry.getKind() == DirEntry.Kind.DIR && logEntryPath.getCopyPath() != null) {
      // Directory node added
      LOGGER.debug(logEntryPath.getPath() + " is a directory. Doing a recursive add");
      // Add directory contents
      addDirectories(entriesToAdd, connection, logEntryPath.getPath() + "/", revision, repositoryService);
    }
    entriesToAdd.add(entry);
  }

  /**
   * Adds all entries in given path.
   * This method will be recursively called by itself.
   *
   * @param entriesToAdd      List containing entries to add.
   * @param connection        Repository
   * @param path              The path to add.
   * @param revision          Revision
   * @param repositoryService Service
   * @throws SventonException if a Subversion error occurs.
   */
  private void addDirectories(final List<DirEntry> entriesToAdd, final SVNConnection connection, final String path,
                              final long revision, final RepositoryService repositoryService) throws SventonException {

    final List<DirEntry> entriesList = repositoryService.list(connection, path, revision).getEntries();

    for (final DirEntry entry : entriesList) {
      entriesToAdd.add(entry);
      if (entry.getKind() == DirEntry.Kind.DIR) {
        final String pathToAdd = path + entry.getName() + "/";
        LOGGER.debug("Adding: " + pathToAdd);
        addDirectories(entriesToAdd, connection, pathToAdd, revision, repositoryService);
      }
    }
  }

  /**
   * Sets the connection factory instance.
   *
   * @param connectionFactory Factory instance.
   */
  @Autowired
  public void setConnectionFactory(final SVNConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  /**
   * Sets the repository service instance.
   *
   * @param repositoryService The service instance.
   */
  @Autowired
  public void setRepositoryService(final RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

}
