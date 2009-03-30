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
package org.sventon.repository.observer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.RepositoryConnectionFactory;
import org.sventon.appl.Application;
import org.sventon.appl.EntryCacheManager;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.cache.CacheException;
import org.sventon.cache.entrycache.EntryCache;
import org.sventon.model.LogEntryActionType;
import org.sventon.model.RepositoryEntry;
import org.sventon.model.RepositoryName;
import org.sventon.repository.RevisionUpdate;
import org.sventon.service.RepositoryService;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.*;

/**
 * Class responsible for updating one or more entry cache instances.
 *
 * @author jesper@sventon.org
 */
public final class EntryCacheUpdater extends AbstractRevisionObserver {

  /**
   * The static logging instance.
   */
  private static final Log LOGGER = LogFactory.getLog(EntryCacheUpdater.class);

  /**
   * The EntryCacheManager instance.
   */
  private final EntryCacheManager entryCacheManager;

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
  private RepositoryConnectionFactory repositoryConnectionFactory;

  /**
   * Constructor.
   *
   * @param entryCacheManager The EntryCacheManager instance.
   * @param application       Application
   */
  public EntryCacheUpdater(final EntryCacheManager entryCacheManager, final Application application) {
    LOGGER.info("Starting");
    this.entryCacheManager = entryCacheManager;
    this.application = application;
  }

  /**
   * Updates the cache with the given revisions.
   *
   * @param revisionUpdate The updated revisions.
   */
  public void update(final RevisionUpdate revisionUpdate) {
    final RepositoryName repositoryName = revisionUpdate.getRepositoryName();

    LOGGER.info("Observer got [" + revisionUpdate.getRevisions().size() + "] updated revision(s) for repository: "
        + repositoryName);

    SVNRepository repository = null;
    try {
      final EntryCache entryCache = entryCacheManager.getCache(repositoryName);
      final RepositoryConfiguration configuration = application.getRepositoryConfiguration(repositoryName);
      repository = repositoryConnectionFactory.createConnection(repositoryName, configuration.getSVNURL(),
          configuration.getCredentials());
      updateInternal(entryCache, repository, revisionUpdate);
    } catch (final Exception ex) {
      LOGGER.warn("Could not update cache instance [" + repositoryName + "]", ex);
    } finally {
      if (repository != null) {
        repository.closeSession();
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
   * @param entryCache     EntryCache.
   * @param repository     Repository.
   * @param revisionUpdate Update
   */
  protected void updateInternal(final EntryCache entryCache, final SVNRepository repository,
                                final RevisionUpdate revisionUpdate) {

    final List<SVNLogEntry> revisions = revisionUpdate.getRevisions();
    final int revisionCount = revisions.size();

    final long firstRevision = revisions.get(0).getRevision();
    final long lastRevision = revisions.get(revisionCount - 1).getRevision();

    if (revisionCount > 0 && firstRevision == 1) {
      LOGGER.info("Starting initial cache population by traversing HEAD: " + revisionUpdate.getRepositoryName());
      doInitialCachePopulation(entryCache, repository, revisionUpdate);
    } else {
      // Initial population has already been performed - only apply changes for now.
      if (lastRevision > entryCache.getLatestCachedRevisionNumber()) {
        // One logEntry is one commit (or revision)
        for (final SVNLogEntry logEntry : revisions) {
          addRevisionToCache(entryCache, repository, logEntry, revisionUpdate.isFlushAfterUpdate());
        }
        LOGGER.debug("Update completed");
      }
    }
  }

  private void addRevisionToCache(final EntryCache entryCache, final SVNRepository repository,
                                  final SVNLogEntry logEntry, boolean flushAfterUpdate) {
    try {
      final long revision = logEntry.getRevision();
      LOGGER.debug("Applying changes in revision [" + revision + "] to cache");

      //noinspection unchecked
      final Map<String, SVNLogEntryPath> map = logEntry.getChangedPaths();
      final List<String> latestPathsList = new ArrayList<String>(map.keySet());
      // Sort the entriesToAdd to apply changes in right order
      Collections.sort(latestPathsList);

      final List<RepositoryEntry> entriesToAdd = new ArrayList<RepositoryEntry>();
      final Map<String, RepositoryEntry.Kind> entriesToDelete = new HashMap<String, RepositoryEntry.Kind>();

      for (final String entryPath : latestPathsList) {
        final SVNLogEntryPath logEntryPath = map.get(entryPath);
        switch (LogEntryActionType.parse(logEntryPath.getType())) {
          case ADDED:
            LOGGER.debug("Adding entry to cache: " + logEntryPath.getPath());
            doEntryCacheAdd(entriesToAdd, repository, logEntryPath, revision);
            break;
          case DELETED:
            LOGGER.debug("Removing deleted entry from cache: " + logEntryPath.getPath());
            doEntryCacheDelete(entriesToDelete, repository, logEntryPath, revision);
            break;
          case REPLACED:
            LOGGER.debug("Replacing entry in cache: " + logEntryPath.getPath());
            doEntryCacheReplace(entriesToAdd, entriesToDelete, repository, logEntryPath, revision);
            break;
          case MODIFIED:
            LOGGER.debug("Updating modified entry in cache: " + logEntryPath.getPath());
            doEntryCacheModify(entriesToAdd, entriesToDelete, repository, logEntryPath, revision);
            break;
          default:
            throw new RuntimeException("Unknown log entry type: " + logEntryPath.getType() + " in rev " + logEntry.getRevision());
        }
      }
      updateAndFlushCache(entriesToAdd, entriesToDelete, revision, entryCache, flushAfterUpdate);
    } catch (SVNException svnex) {
      LOGGER.error("Unable to update entryCache", svnex);
    }
  }

  private void doInitialCachePopulation(EntryCache entryCache, SVNRepository repository, RevisionUpdate revisionUpdate) {

    try {
      entryCache.clear();
      long revision = repositoryService.getLatestRevision(repository);
      final List<RepositoryEntry> entriesToAdd = new ArrayList<RepositoryEntry>();
      addDirectories(entriesToAdd, repository, "/", revision, repositoryService);
      updateAndFlushCache(entriesToAdd, Collections.EMPTY_MAP, revision, entryCache, revisionUpdate.isFlushAfterUpdate());
    } catch (SVNException svnex) {
      LOGGER.error("Unable to populate cache", svnex);
    }
  }

  private void updateAndFlushCache(final List<RepositoryEntry> entriesToAdd,
                                   final Map<String, RepositoryEntry.Kind> entriesToDelete,
                                   final long revision, EntryCache entryCache, boolean flushAfterUpdate) {
    entryCache.removeAndAdd(entriesToAdd,entriesToDelete);
    entryCache.setLatestCachedRevisionNumber(revision);

    if (flushAfterUpdate) {
      try {
        entryCache.flush();
      } catch (final CacheException ce) {
        LOGGER.error("Unable to flush cache", ce);
      }
    }
  }

  /**
   * Modifies an entry (file or directory) in the cache.
   *
   * @param entriesToAdd    List of entries to add.
   * @param entriesToDelete List of entries to delete.
   * @param repository      Repository
   * @param logEntryPath    The log entry path
   * @param revision        The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doEntryCacheModify(final List<RepositoryEntry> entriesToAdd,
                                  final Map<String, RepositoryEntry.Kind> entriesToDelete,
                                  final SVNRepository repository, final SVNLogEntryPath logEntryPath,
                                  final long revision) throws SVNException {

    entriesToDelete.put(logEntryPath.getPath(), RepositoryEntry.Kind.ANY);
    final RepositoryEntry entry = repositoryService.getEntryInfo(repository, logEntryPath.getPath(), revision);
    entriesToAdd.add(entry);
  }

  /**
   * Replaces an entry (file or directory) in the cache.
   *
   * @param entriesToAdd    Entries
   * @param entriesToDelete List of entries to delete.
   * @param repository      Repository
   * @param logEntryPath    The log entry path
   * @param revision        The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doEntryCacheReplace(final List<RepositoryEntry> entriesToAdd,
                                   final Map<String, RepositoryEntry.Kind> entriesToDelete,
                                   final SVNRepository repository, final SVNLogEntryPath logEntryPath,
                                   final long revision) throws SVNException {

    doEntryCacheModify(entriesToAdd, entriesToDelete, repository, logEntryPath, revision);
  }

  /**
   * Deletes an entry (file or directory) from the cache.
   *
   * @param entriesToDelete List of entries to delete.
   * @param repository      Repository
   * @param logEntryPath    The log entry path
   * @param revision        The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doEntryCacheDelete(final Map<String, RepositoryEntry.Kind> entriesToDelete,
                                  final SVNRepository repository, final SVNLogEntryPath logEntryPath,
                                  final long revision) throws SVNException {

    // Have to find out if deleted entry was a file or directory
    final long previousRevision = revision - 1;
    RepositoryEntry deletedEntry;

    try {
      deletedEntry = repositoryService.getEntryInfo(repository, logEntryPath.getPath(), previousRevision);
      entriesToDelete.put(logEntryPath.getPath(), deletedEntry.getKind());
    } catch (SVNException e) {
      if (SVNErrorCode.ENTRY_NOT_FOUND.equals(e.getErrorMessage().getErrorCode())) {
        LOGGER.debug("Entry [" + logEntryPath.getPath() + "] does not exist in revision [" + previousRevision + "] - nothing to remove");
      } else {
        throw e;
      }
    }
  }

  /**
   * Adds an entry (file or directory) to the cache.
   *
   * @param entriesToAdd Entries
   * @param repository   Repository
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doEntryCacheAdd(final List<RepositoryEntry> entriesToAdd,
                               final SVNRepository repository, final SVNLogEntryPath logEntryPath,
                               final long revision) throws SVNException {

    // Have to find out if added entry was a file or directory
    final RepositoryEntry entry = repositoryService.getEntryInfo(repository, logEntryPath.getPath(), revision);

    // If the entry is a directory and a copyPath exists, the entry is
    // a moved or copied directory (branch). In that case we have to recursively
    // add the entry. If entry is a directory but does not have a copyPath
    // the contents will be added one by one as single entriesToAdd.
    if (entry.getKind() == RepositoryEntry.Kind.DIR && logEntryPath.getCopyPath() != null) {
      // Directory node added
      LOGGER.debug(logEntryPath.getPath() + " is a directory. Doing a recursive add");
      // Add directory contents
      addDirectories(entriesToAdd, repository, logEntryPath.getPath() + "/", revision, repositoryService);
    }
    entriesToAdd.add(entry);
  }

  /**
   * Adds all entries in given path.
   * This method will be recursively called by itself.
   *
   * @param entriesToAdd      List containing entries to add.
   * @param repository        Repository
   * @param path              The path to add.
   * @param revision          Revision
   * @param repositoryService Service
   * @throws SVNException if a Subversion error occurs.
   */
  private void addDirectories(final List<RepositoryEntry> entriesToAdd, final SVNRepository repository, final String path,
                              final long revision, final RepositoryService repositoryService) throws SVNException {

    final List<RepositoryEntry> entriesList = repositoryService.list(repository, path, revision, null);
    for (final RepositoryEntry entry : entriesList) {
      entriesToAdd.add(entry);
      if (entry.getKind() == RepositoryEntry.Kind.DIR) {
        final String pathToAdd = path + entry.getName() + "/";
        LOGGER.debug("Adding: " + pathToAdd);
        addDirectories(entriesToAdd, repository, pathToAdd, revision, repositoryService);
      }
    }
  }

  /**
   * Sets the repository connection factory instance.
   *
   * @param repositoryConnectionFactory Factory instance.
   */
  public void setRepositoryConnectionFactory(final RepositoryConnectionFactory repositoryConnectionFactory) {
    this.repositoryConnectionFactory = repositoryConnectionFactory;
  }

  /**
   * Sets the repository service instance.
   *
   * @param repositoryService The service instance.
   */
  public void setRepositoryService(final RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

}
