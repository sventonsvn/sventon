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
package de.berlios.sventon.repository.cache;

import de.berlios.sventon.repository.RepositoryConfiguration;
import de.berlios.sventon.repository.RepositoryEntry;
import static de.berlios.sventon.repository.RepositoryEntry.Kind.dir;
import de.berlios.sventon.repository.RepositoryFactory;
import de.berlios.sventon.ctrl.LogEntryActionType;
import de.berlios.sventon.util.PathUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.*;

/**
 * Service class used to access the caches.
 * <p/>
 * Responsibility: Start/stop the transaction, trigger cache update and perform search.
 *
 * @author jesper@users.berlios.de
 */
public class CacheServiceImpl implements CacheService {

  private SVNRepository repository;
  private RepositoryConfiguration configuration;
  private EntryCache entryCache;
  private CommitMessageCache commitMessageCache;

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  private boolean updating = false;

  public CacheServiceImpl() {
    logger.info("Starting cache service");
  }

  public void setRepositoryConfiguration(final RepositoryConfiguration configuration) {
    this.configuration = configuration;
  }

  public void setEntryCache(final EntryCache entryCache) {
    this.entryCache = entryCache;
  }

  public void setCommitMessageCache(final CommitMessageCache commitMessageCache) {
    this.commitMessageCache = commitMessageCache;
  }

  /**
   * Sets the repository. This method exists for testing purposes only!
   *
   * @param repository The repository
   */
  protected void setRepository(final SVNRepository repository) {
    this.repository = repository;
  }

  /**
   * {@inheritDoc}
   */
  public synchronized void updateCaches() throws CacheException {

/*
* Kolla att cachen är initierat
* Toggla isUpdating-flagga
* Avgöra om cachen är uptodate
* Antingen populera eller trigga update
* Toggla isUpdating-flaggan igen
*/

    if (!isConnectionEstablished() || !configuration.isCacheUsed()) {
      return;
    }

    // update entryCache
    // update commitMessageCache

    updating = true;
    try {
      //TODO: Load cache (if exists)

      long headRevision = repository.getLatestRevision();
      logger.debug("HEAD revision: " + headRevision);

      if (entryCache.getUnmodifiableEntries().size() == 0
          || !configuration.getUrl().equals(entryCache.getRepositoryUrl())
          || entryCache.getCachedRevision() > headRevision) {
        // cache is just created and does not contain any entries
        // or the repository URL has changed in the config properties
        // or the repository revision is LOWER than the cached revision
        logger.info("Populating cache");
        entryCache.setRepositoryURL(configuration.getUrl());
        logger.debug("Caching url: " + entryCache.getRepositoryUrl());
        populate("/", headRevision);
        entryCache.setCachedRevision(headRevision);
        logger.info("Number of cached entries: " + entryCache.getUnmodifiableEntries().size());
        //TODO: Store cache
      } else if (entryCache.getCachedRevision() < headRevision) {
        logger.debug("Updating cache from revision [" + entryCache.getCachedRevision()
            + "] to [" + headRevision+ "]");
        update(headRevision);
      }
      logger.debug("Cache is up-to-date");
    } catch (SVNException svnex) {
      throw new CacheException("Unable to update caches", svnex);
    } finally {
      updating = false;
    }
  }

  /**
   * {@inheritDoc}
   */
  public boolean isUpdating() {
    return updating;
  }


  /**
   * Checks if the repository connection is properly initialized.
   * If not, a connection will be created.
   */
  private boolean isConnectionEstablished() {
    if (repository == null) {
      try {
        logger.debug("Establishing repository connection");
        repository = RepositoryFactory.INSTANCE.getRepository(configuration);
      } catch (SVNException svne) {
        logger.warn("Could not establish repository connection", svne);
      }
      if (repository == null) {
        logger.info("Repository not configured yet.");
        return false;
      }
    }
    return true;
  }


  /**
   * Checks if the cache is properly initialized.
   * If not, the cache will be loaded.
   *
   * @throws Exception if unable to load cache.
   */
  private void assertCacheIsInitialized() throws Exception {
  }

  /**
   * Populates the cache by getting all entries in given path
   * and adding them to the cache. This method will be recursively
   * called by itself.
   *
   * @param path The path to cache.
   * @throws SVNException if a Subversion error occurs.
   */
  @SuppressWarnings("unchecked")
  private void populate(final String path, final long revision) throws SVNException {
    final List<SVNDirEntry> entriesList = Collections.checkedList(new ArrayList<SVNDirEntry>(), SVNDirEntry.class);

    entriesList.addAll(repository.getDir(path, revision, null, (Collection) null));
    for (SVNDirEntry entry : entriesList) {
      final RepositoryEntry newEntry = new RepositoryEntry(entry, path, null);
      if (!entryCache.add(newEntry)) {
        logger.warn("Unable to add already existing entry to cache: " + newEntry.toString());
      }
      if (entry.getKind() == SVNNodeKind.DIR) {
        populate(path + entry.getName() + "/", revision);
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
   * @throws SVNException if Subversion error occurs.
   */
  @SuppressWarnings("unchecked")
  private synchronized void update(final long headRevision) throws SVNException {

    final String[] targetPaths = new String[]{"/"}; // the path to log
    final List<SVNLogEntry> logEntries = (List<SVNLogEntry>) repository.log(targetPaths,
        null, entryCache.getCachedRevision() + 1, headRevision, true, false);

    // One logEntry is one commit (or revision)
    for (SVNLogEntry logEntry : logEntries) {
      long revision = logEntry.getRevision();
      logger.debug("Applying changes from revision " + revision + " to cache");
      final Map<String, SVNLogEntryPath> map = logEntry.getChangedPaths();
      final List<String> latestPathsList = new ArrayList<String>(map.keySet());
      // Sort the entries to apply changes in right order
      Collections.sort(latestPathsList);

      for (String entryPath : latestPathsList) {
        final SVNLogEntryPath logEntryPath = map.get(entryPath);
        switch (LogEntryActionType.valueOf(String.valueOf(logEntryPath.getType()))) {
          case A :
            logger.debug("Adding entry to cache: " + logEntryPath.getPath());
            doCacheAdd(logEntryPath, revision);
            break;
          case D :
            logger.debug("Removing deleted entry from cache: " + logEntryPath.getPath());
            doCacheDelete(logEntryPath, revision);
            break;
          case R :
            logger.debug("Replacing entry in cache: " + logEntryPath.getPath());
            doCacheReplace(logEntryPath, revision);
            break;
          case M :
            logger.debug("Updating modified entry in cache: " + logEntryPath.getPath());
            doCacheModify(logEntryPath, revision);
            break;
          default :
            throw new RuntimeException("Unknown log entry type: " + logEntryPath.getType() + " in rev " + logEntry.getRevision());
        }
      }
      entryCache.setCachedRevision(headRevision);
    }
  }

  /**
   * Modifies an entry (file or directory) in the cache.
   *
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doCacheModify(final SVNLogEntryPath logEntryPath, final long revision) throws SVNException {
    entryCache.removeByName(logEntryPath.getPath(), false);
    entryCache.add(new RepositoryEntry(repository.info(logEntryPath.getPath(), revision),
        PathUtil.getPathPart(logEntryPath.getPath()), null));
  }

  /**
   * Replaces an entry (file or directory) in the cache.
   *
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doCacheReplace(final SVNLogEntryPath logEntryPath, final long revision) throws SVNException {
    doCacheModify(logEntryPath, revision);
  }

  /**
   * Deletes an entry (file or directory) from the cache.
   *
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doCacheDelete(final SVNLogEntryPath logEntryPath, final long revision) throws SVNException {
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
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doCacheAdd(final SVNLogEntryPath logEntryPath, final long revision) throws SVNException {
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
      populate(logEntryPath.getPath() + "/", revision);
    } else {
      // Single entry added
      entryCache.add(new RepositoryEntry(addedEntry, PathUtil.getPathPart(logEntryPath.getPath()), null));
    }
  }


  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String searchString) throws CacheException {
    updateCaches();
    return entryCache.findByPattern("/" + ".*?" + searchString + ".*?", RepositoryEntry.Kind.any, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String searchString, final String startDir) throws CacheException {
    updateCaches();
    return entryCache.findByPattern(startDir + ".*?" + searchString + ".*?", RepositoryEntry.Kind.any, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String searchString, final String startDir, final Integer limit) throws CacheException {
    updateCaches();
    return entryCache.findByPattern(startDir + ".*?" + searchString + ".*?", RepositoryEntry.Kind.any, limit);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findDirectories(final String fromPath) throws CacheException {
    updateCaches();
    return entryCache.findByPattern(fromPath + ".*?", dir, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<Object> find(final String searchString) throws CacheException {
    updateCaches();
    return commitMessageCache.find(searchString);
  }


}
