/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.repository.cache.entrycache;

import de.berlios.sventon.config.ApplicationConfiguration;
import de.berlios.sventon.repository.AbstractRevisionObserver;
import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.repository.RevisionUpdate;
import de.berlios.sventon.repository.RepositoryFactory;
import de.berlios.sventon.repository.cache.CacheException;
import de.berlios.sventon.util.PathUtil;
import de.berlios.sventon.web.model.LogEntryActionType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.*;

/**
 * Class responsible for updating one or more entry cache instances.
 *
 * @author jesper@users.berlios.de
 */
public class EntryCacheUpdater extends AbstractRevisionObserver {

  /**
   * The static logging instance.
   */
  private static final Log logger = LogFactory.getLog(EntryCacheUpdater.class);

  /**
   * The EntryCacheManager instance.
   */
  private final EntryCacheManager entryCacheManager;

  /**
   * Application configuration instance.
   */
  private ApplicationConfiguration configuration;

  /**
   * Constructor.
   *
   * @param entryCacheManager The EntryCacheManager instance.
   * @param configuration     ApplicationConfiguration instance.
   */
  public EntryCacheUpdater(final EntryCacheManager entryCacheManager, final ApplicationConfiguration configuration) {
    logger.info("Starting");
    this.entryCacheManager = entryCacheManager;
    this.configuration = configuration;
    for (final String instanceName : configuration.getInstanceNames()) {
      logger.debug("Initializing cache instance: " + instanceName);
      try {
        this.entryCacheManager.getCache(instanceName);
      } catch (CacheException ce) {
        logger.warn("Unable to initialize instance");
      }
    }
  }

  /**
   * Updates the cache to HEAD revision.
   * A Subversion <i>log</i> command will be performed and
   * the cache will be updated accordingly.
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
   * @param revisionUpdate The updated revisions.
   */
  public void update(final RevisionUpdate revisionUpdate) {
    logger.info("Observer got [" + revisionUpdate.getRevisions().size() + "] updated revision(s) for instance: "
        + revisionUpdate.getInstanceName());

    if (configuration == null) {
      logger.warn("Method setConfiguration() has not yet been called!");
    }

    try {
      final EntryCache entryCache = entryCacheManager.getCache(revisionUpdate.getInstanceName());

      final SVNRepository repository = RepositoryFactory.INSTANCE.getRepository(
          configuration.getInstanceConfiguration(revisionUpdate.getInstanceName()),
          configuration.getSVNConfigurationPath());

      updateInternal(entryCache, repository, revisionUpdate);
    } catch (final Exception ex) {
      logger.warn("Could not update cache instance [" + revisionUpdate.getInstanceName() + "]", ex);
      return;
    }
  }

  /**
   * Internal update method. Made protected for testing reasons only.
   *
   * @param entryCache     EntryCache instance
   * @param revisionUpdate Update
   */
  protected static void updateInternal(final EntryCache entryCache, final SVNRepository repository,
                                       final RevisionUpdate revisionUpdate) {

    final List<SVNLogEntry> revisions = revisionUpdate.getRevisions();
    final int revisionCount = revisions.size();
    final long firstRevision = revisions.get(0).getRevision();
    final long lastRevision = revisions.get(revisionCount - 1).getRevision();

    if (revisionCount > 0 && firstRevision == 1) {
      logger.info("Starting initial population of cache: " + revisionUpdate.getInstanceName());
      try {
        addDirectories(entryCache, repository, "/", lastRevision);
        entryCache.setCachedRevision(lastRevision);
      } catch (SVNException svnex) {
        logger.error("Unable to populate cache", svnex);
      }
      logger.info("Cache population done");
    } else {
      // Initial population has already been performed - only apply changes for now.
      if (lastRevision < entryCache.getCachedRevision()) {
        throw new IllegalStateException("Revision [" + lastRevision + "] is older than last cached revision ["
            + entryCache.getCachedRevision() + "]. Has the repository URL changed? Delete all cache files to "
            + "trigger a complete cache rebuild");
      }

      // One logEntry is one commit (or revision)
      for (final SVNLogEntry logEntry : revisions) {
        try {
          long revision = logEntry.getRevision();
          logger.debug("Applying changes in revision [" + revision + "] to cache");

          final Map<String, SVNLogEntryPath> map = logEntry.getChangedPaths();
          final List<String> latestPathsList = new ArrayList<String>(map.keySet());
          // Sort the entries to apply changes in right order
          Collections.sort(latestPathsList);

          for (final String entryPath : latestPathsList) {
            final SVNLogEntryPath logEntryPath = map.get(entryPath);
            switch (LogEntryActionType.parse(logEntryPath.getType())) {
              case ADDED:
                logger.debug("Adding entry to cache: " + logEntryPath.getPath());
                doEntryCacheAdd(entryCache, repository, logEntryPath, revision);
                break;
              case DELETED:
                logger.debug("Removing deleted entry from cache: " + logEntryPath.getPath());
                doEntryCacheDelete(entryCache, repository, logEntryPath, revision);
                break;
              case REPLACED:
                logger.debug("Replacing entry in cache: " + logEntryPath.getPath());
                doEntryCacheReplace(entryCache, repository, logEntryPath, revision);
                break;
              case MODIFIED:
                logger.debug("Updating modified entry in cache: " + logEntryPath.getPath());
                doEntryCacheModify(entryCache, repository, logEntryPath, revision);
                break;
              default :
                throw new RuntimeException("Unknown log entry type: " + logEntryPath.getType() + " in rev " + logEntry.getRevision());
            }
          }
        } catch (SVNException svnex) {
          logger.error("Unable to update entryCache", svnex);
        }
      }
    }
  }

  /**
   * Modifies an entry (file or directory) in the cache.
   *
   * @param entryCache   The cache instance.
   * @param repository   Repository
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws org.tmatesoft.svn.core.SVNException
   *          if subversion error occur.
   */
  private static void doEntryCacheModify(final EntryCache entryCache, final SVNRepository repository,
                                         final SVNLogEntryPath logEntryPath, final long revision) throws SVNException {

    entryCache.removeByName(logEntryPath.getPath(), false);
    entryCache.add(new RepositoryEntry(repository.info(logEntryPath.getPath(), revision),
        PathUtil.getPathPart(logEntryPath.getPath()), null));
  }

  /**
   * Replaces an entry (file or directory) in the cache.
   *
   * @param entryCache   The cache instance.
   * @param repository   Repository
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private static void doEntryCacheReplace(final EntryCache entryCache, final SVNRepository repository,
                                          final SVNLogEntryPath logEntryPath, final long revision) throws SVNException {

    doEntryCacheModify(entryCache, repository, logEntryPath, revision);
  }

  /**
   * Deletes an entry (file or directory) from the cache.
   *
   * @param entryCache   The cache instance.
   * @param repository   Repository
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private static void doEntryCacheDelete(final EntryCache entryCache, final SVNRepository repository,
                                         final SVNLogEntryPath logEntryPath, final long revision) throws SVNException {

    // Have to find out if deleted entry was a file or directory
    final SVNDirEntry deletedEntry = repository.info(logEntryPath.getPath(), revision - 1);
    if (RepositoryEntry.Kind.valueOf(deletedEntry.getKind().toString()) == RepositoryEntry.Kind.dir) {
      // Directory node deleted
      logger.debug(logEntryPath.getPath() + " is a directory. Doing a recursive delete");
      entryCache.removeByName(logEntryPath.getPath(), true);
    } else {
      // Single entry delete
      entryCache.removeByName(logEntryPath.getPath(), false);
    }
  }

  /**
   * Adds an entry (file or directory) to the cache.
   *
   * @param entryCache   The cache instance.
   * @param repository   Repository
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private static void doEntryCacheAdd(final EntryCache entryCache, final SVNRepository repository,
                                      final SVNLogEntryPath logEntryPath, final long revision) throws SVNException {

    // Have to find out if added entry was a file or directory
    final SVNDirEntry addedEntry = repository.info(logEntryPath.getPath(), revision);

    // If the entry is a directory and a copyPath exists, the entry is
    // a moved or copied directory (branch). In that case we have to recursively
    // add the entry. If entry is a directory but does not have a copyPath
    // the contents will be added one by one as single entries.
    if (RepositoryEntry.Kind.valueOf(addedEntry.getKind().toString()) == RepositoryEntry.Kind.dir
        && logEntryPath.getCopyPath() != null) {
      // Directory node added
      logger.debug(logEntryPath.getPath() + " is a directory. Doing a recursive add");
      entryCache.add(new RepositoryEntry(addedEntry, PathUtil.getPathPart(logEntryPath.getPath()), null));
      // Add directory contents
      addDirectories(entryCache, repository, logEntryPath.getPath() + "/", revision);
    } else {
      // Single entry added
      entryCache.add(new RepositoryEntry(addedEntry, PathUtil.getPathPart(logEntryPath.getPath()), null));
    }
  }

  /**
   * Adds all entries in given path.
   * This method will be recursively called by itself.
   *
   * @param entryCache The cache instance.
   * @param repository Repository
   * @param path       The path to add.
   * @throws org.tmatesoft.svn.core.SVNException
   *          if a Subversion error occurs.
   */
  @SuppressWarnings("unchecked")
  private static void addDirectories(final EntryCache entryCache, final SVNRepository repository, final String path,
                                     final long revision) throws SVNException {

    final List<SVNDirEntry> entriesList = Collections.checkedList(new ArrayList<SVNDirEntry>(), SVNDirEntry.class);

    entriesList.addAll(repository.getDir(path, revision, null, (Collection) null));
    for (final SVNDirEntry entry : entriesList) {
      final RepositoryEntry newEntry = new RepositoryEntry(entry, path, null);
      if (!entryCache.add(newEntry)) {
        logger.warn("Unable to add already existing entry to cache: " + newEntry.toString());
      }
      if (entry.getKind() == SVNNodeKind.DIR) {
        addDirectories(entryCache, repository, path + entry.getName() + "/", revision);
      }
    }
  }
}
