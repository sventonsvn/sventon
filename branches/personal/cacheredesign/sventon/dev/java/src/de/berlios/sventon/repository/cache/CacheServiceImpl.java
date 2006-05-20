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

import de.berlios.sventon.web.ctrl.LogEntryActionType;
import de.berlios.sventon.repository.RepositoryConfiguration;
import de.berlios.sventon.repository.RepositoryEntry;
import static de.berlios.sventon.repository.RepositoryEntry.Kind.dir;
import de.berlios.sventon.repository.RepositoryFactory;
import de.berlios.sventon.repository.CommitMessage;
import de.berlios.sventon.repository.cache.commitmessagecache.CommitMessageCache;
import de.berlios.sventon.repository.cache.entrycache.EntryCache;
import de.berlios.sventon.repository.cache.entrycache.EntryCacheReader;
import de.berlios.sventon.repository.cache.entrycache.EntryCacheWriter;
import de.berlios.sventon.util.PathUtil;
import de.berlios.sventon.cache.ObjectCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.*;

/**
 * Service class used to access the caches.
 *
 * @author jesper@users.berlios.de
 */
public class CacheServiceImpl implements CacheService {

  private SVNRepository repository;
  private RepositoryConfiguration configuration;
  private EntryCache entryCache;
  private CommitMessageCache commitMessageCache;
  private EntryCacheReader entryCacheReader;
  private EntryCacheWriter entryCacheWriter;
  public static final String LAST_CACHED_LOG_REVISION_CACHE_KEY = "lastCachedLogRevision";

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Determines whether the index is buzy updating or not.
   */
  private boolean updating = false;

  /**
   * The ObjectCache instance.
   */
  private ObjectCache objectCache;

  /**
   * Constructor.
   */
  public CacheServiceImpl() {
    logger.info("Starting cache service");
  }

  /**
   * Sets the repository configuration.
   *
   * @param configuration Configuration
   */
  public void setRepositoryConfiguration(final RepositoryConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Sets the entry cache instance.
   *
   * @param entryCache EntryCache instance.
   */
  public void setEntryCache(final EntryCache entryCache) {
    this.entryCache = entryCache;
  }

  /**
   * Sets the commit message cache instance.
   *
   * @param commitMessageCache The cache instance.
   */
  public void setCommitMessageCache(final CommitMessageCache commitMessageCache) {
    this.commitMessageCache = commitMessageCache;
  }

  /**
   * Sets the cache instance.
   *
   * @param objectCache The cache instance.
   */
  public void setObjectCache(final ObjectCache objectCache) {
    this.objectCache = objectCache;
  }

  /**
   * Initializes the cache service.
   */
  public synchronized void initialize() {
    if (entryCache.isInitialized()) {
      entryCacheReader = new EntryCacheReader(entryCache);
      entryCacheWriter = new EntryCacheWriter(entryCache);
    }
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

    if (!isConnectionEstablished() || !configuration.isCacheUsed()) {
      // Silently return. Either cache is disabled or sventon
      // has not yet been configured.
      return;
    }

    try {
      updating = true;
      long headRevision = repository.getLatestRevision();

      if (objectCache.get(LAST_CACHED_LOG_REVISION_CACHE_KEY) == null) {
        logger.debug("No cached commit messages - populating cache");
        long revisionCount = repository.log(new String[]{"/"}, headRevision, 0, true, false, new ISVNLogEntryHandler() {
          public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
            try {
              //TODO: SVNLogEntry is not serializable yet (in javasvn trunk only so far)
              //objectCache.put("cachedRevision-" + logEntry.getRevision(), logEntry);
              commitMessageCache.add(new CommitMessage(logEntry.getRevision(), logEntry.getMessage()));
            } catch (CacheException ce) {
              logger.warn("Unable to add commit message to cache", ce);
            }
          }
        });
        logger.debug("Revisions cached: " + revisionCount);
        if (revisionCount > 0) {
          logger.debug("Updating '" + LAST_CACHED_LOG_REVISION_CACHE_KEY + "' to [" + headRevision + "]");
          objectCache.put(LAST_CACHED_LOG_REVISION_CACHE_KEY, headRevision);
        }
      }

      if (entryCacheReader.getEntriesCount() == 0
          || !configuration.getUrl().equals(entryCacheReader.getRepositoryUrl())
          || entryCacheReader.getCachedRevision() > headRevision) {
        // cache is just created and does not contain any entries
        // or the repository URL has changed in the config properties
        // or the repository revision is LOWER than the cached revision
        logger.info("Populating entry cache");
        entryCacheWriter.setRepositoryURL(configuration.getUrl());
        logger.debug("Caching url: " + entryCacheReader.getRepositoryUrl());
        populateEntryCache("/", headRevision);
        entryCacheWriter.setCachedRevision(headRevision);
        logger.info("Number of cached entries: " + entryCacheReader.getEntriesCount());
        //TODO: Flush cache file
      } else if (entryCacheReader.getCachedRevision() < headRevision) {
        logger.debug("Updating cache from revision [" + entryCacheReader.getCachedRevision()
            + "] to [" + headRevision + "]");
        update(headRevision);
        //TODO: Flush cache file
      }


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
        logger.info("Repository not configured yet");
        return false;
      }
    }
    return true;
  }

  /**
   * Populates the entry cache by getting all entries in given path
   * and adding them to the cache. This method will be recursively
   * called by itself.
   *
   * @param path The path to cache.
   * @throws SVNException if a Subversion error occurs.
   */
  @SuppressWarnings("unchecked")
  private void populateEntryCache(final String path, final long revision) throws SVNException {
    final List<SVNDirEntry> entriesList = Collections.checkedList(new ArrayList<SVNDirEntry>(), SVNDirEntry.class);

    entriesList.addAll(repository.getDir(path, revision, null, (Collection) null));
    for (SVNDirEntry entry : entriesList) {
      final RepositoryEntry newEntry = new RepositoryEntry(entry, path, null);
      if (!entryCacheWriter.add(newEntry)) {
        logger.warn("Unable to add already existing entry to cache: " + newEntry.toString());
      }
      if (entry.getKind() == SVNNodeKind.DIR) {
        populateEntryCache(path + entry.getName() + "/", revision);
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
  private void update(final long headRevision) throws SVNException {

    final String[] targetPaths = new String[]{"/"}; // the path to log
    final List<SVNLogEntry> logEntries = (List<SVNLogEntry>) repository.log(targetPaths,
        null, entryCacheReader.getCachedRevision() + 1, headRevision, true, false);

    // One logEntry is one commit (or revision)
    for (SVNLogEntry logEntry : logEntries) {
      long revision = logEntry.getRevision();
      logger.debug("Applying changes from revision [" + revision + "] to cache");

      long lastCachedLogRevision = (Long) objectCache.get(LAST_CACHED_LOG_REVISION_CACHE_KEY);
      if (lastCachedLogRevision < revision) {
        logger.debug("Updating commitMessageCache");
        try {
          commitMessageCache.add(new CommitMessage(revision, logEntry.getMessage()));
        } catch (CacheException ce) {
          logger.warn("Unable to add commit message to cache", ce);
        }
      }

      final Map<String, SVNLogEntryPath> map = logEntry.getChangedPaths();
      final List<String> latestPathsList = new ArrayList<String>(map.keySet());
      // Sort the entries to apply changes in right order
      Collections.sort(latestPathsList);

      for (String entryPath : latestPathsList) {
        final SVNLogEntryPath logEntryPath = map.get(entryPath);
        switch (LogEntryActionType.valueOf(String.valueOf(logEntryPath.getType()))) {
          case A :
            logger.debug("Adding entry to cache: " + logEntryPath.getPath());
            doEntryCacheAdd(logEntryPath, revision);
            break;
          case D :
            logger.debug("Removing deleted entry from cache: " + logEntryPath.getPath());
            doEntryCacheDelete(logEntryPath, revision);
            break;
          case R :
            logger.debug("Replacing entry in cache: " + logEntryPath.getPath());
            doEntryCacheReplace(logEntryPath, revision);
            break;
          case M :
            logger.debug("Updating modified entry in cache: " + logEntryPath.getPath());
            doEntryCacheModify(logEntryPath, revision);
            break;
          default :
            throw new RuntimeException("Unknown log entry type: " + logEntryPath.getType() + " in rev " + logEntry.getRevision());
        }
      }
    }
    entryCacheWriter.setCachedRevision(headRevision);
    objectCache.put(LAST_CACHED_LOG_REVISION_CACHE_KEY, headRevision);
  }

  /**
   * Modifies an entry (file or directory) in the cache.
   *
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doEntryCacheModify(final SVNLogEntryPath logEntryPath, final long revision) throws SVNException {
    entryCacheWriter.removeByName(logEntryPath.getPath(), false);
    entryCacheWriter.add(new RepositoryEntry(repository.info(logEntryPath.getPath(), revision),
        PathUtil.getPathPart(logEntryPath.getPath()), null));
  }

  /**
   * Replaces an entry (file or directory) in the cache.
   *
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doEntryCacheReplace(final SVNLogEntryPath logEntryPath, final long revision) throws SVNException {
    doEntryCacheModify(logEntryPath, revision);
  }

  /**
   * Deletes an entry (file or directory) from the cache.
   *
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doEntryCacheDelete(final SVNLogEntryPath logEntryPath, final long revision) throws SVNException {
    // Have to find out if deleted entry was a file or directory
    final SVNDirEntry deletedEntry = repository.info(logEntryPath.getPath(), revision - 1);
    if (RepositoryEntry.Kind.valueOf(deletedEntry.getKind().toString()) == RepositoryEntry.Kind.dir) {
      // Directory node deleted
      logger.debug(logEntryPath.getPath() + " is a directory. Doing a recursive delete");
      entryCacheWriter.removeByName(logEntryPath.getPath(), true);
    } else {
      // Single entry delete
      entryCacheWriter.removeByName(logEntryPath.getPath(), false);
    }
  }

  /**
   * Adds an entry (file or directory) to the cache.
   *
   * @param logEntryPath The log entry path
   * @param revision     The log revision
   * @throws SVNException if subversion error occur.
   */
  private void doEntryCacheAdd(final SVNLogEntryPath logEntryPath, final long revision) throws SVNException {
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
      entryCacheWriter.add(new RepositoryEntry(addedEntry, PathUtil.getPathPart(logEntryPath.getPath()), null));
      // Add directory contents
      populateEntryCache(logEntryPath.getPath() + "/", revision);
    } else {
      // Single entry added
      entryCacheWriter.add(new RepositoryEntry(addedEntry, PathUtil.getPathPart(logEntryPath.getPath()), null));
    }
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String searchString) throws CacheException {
    updateCaches();
    return entryCacheReader.findByPattern("/" + ".*?" + searchString + ".*?", RepositoryEntry.Kind.any, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String searchString, final String startDir) throws CacheException {
    updateCaches();
    return entryCacheReader.findByPattern(startDir + ".*?" + searchString + ".*?", RepositoryEntry.Kind.any, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String searchString, final String startDir, final Integer limit) throws CacheException {
    updateCaches();
    return entryCacheReader.findByPattern(startDir + ".*?" + searchString + ".*?", RepositoryEntry.Kind.any, limit);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findDirectories(final String fromPath) throws CacheException {
    updateCaches();
    return entryCacheReader.findByPattern(fromPath + ".*?", dir, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<CommitMessage> find(final String queryString) throws CacheException {
    updateCaches();
    return commitMessageCache.find(queryString);
  }

}
