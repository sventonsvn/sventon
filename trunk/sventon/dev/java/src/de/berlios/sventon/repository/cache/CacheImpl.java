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

import de.berlios.sventon.repository.LogMessage;
import de.berlios.sventon.repository.RepositoryEntry;
import static de.berlios.sventon.repository.RepositoryEntry.Kind.dir;
import de.berlios.sventon.repository.cache.entrycache.EntryCacheManager;
import de.berlios.sventon.repository.cache.logmessagecache.LogMessageCacheManager;
import de.berlios.sventon.repository.cache.revisioncache.RevisionCacheManager;
import de.berlios.sventon.repository.cache.revisioncache.RevisionCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.List;

/**
 * Gateway class used to access the caches.
 *
 * @author jesper@users.berlios.de
 */
public class CacheImpl implements Cache {

  private EntryCacheManager entryCacheManager;
  private LogMessageCacheManager logMessageCacheManager;
  private RevisionCacheManager revisionCacheManager;

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Constructor.
   */
  public CacheImpl() {
    logger.info("Starting cache");
  }

  /**
   * Sets the entry cache manager instance.
   *
   * @param entryCacheManager EntryCacheManager instance.
   */
  public void setEntryCacheManager(final EntryCacheManager entryCacheManager) {
    this.entryCacheManager = entryCacheManager;
  }

  /**
   * Sets the log message cache manager instance.
   *
   * @param logMessageCacheManager LogMessageCacheManager instance.
   */
  public void setLogMessageCacheManager(final LogMessageCacheManager logMessageCacheManager) {
    this.logMessageCacheManager = logMessageCacheManager;
  }

  /**
   * Sets the revision cache manager instance.
   *
   * @param revisionCacheManager RevisionCacheManager instance.
   */
  public void setRevisionCache(final RevisionCacheManager revisionCacheManager) {
    this.revisionCacheManager = revisionCacheManager;
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String searchString) throws CacheException {
    return entryCacheManager.getCache("defaultsvn").findByPattern("/" + ".*?" + searchString + ".*?", RepositoryEntry.Kind.any, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntryByCamelCase(final CamelCasePattern pattern, final String startDir) throws CacheException {
    return entryCacheManager.getCache("defaultsvn").findByPattern(".*" + startDir + pattern.getPattern(), RepositoryEntry.Kind.any, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String searchString, final String startDir) throws CacheException {
    return entryCacheManager.getCache("defaultsvn").findByPattern(startDir + ".*?" + searchString + ".*?", RepositoryEntry.Kind.any, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String searchString, final String startDir, final Integer limit) throws CacheException {
    return entryCacheManager.getCache("defaultsvn").findByPattern(startDir + ".*?" + searchString + ".*?", RepositoryEntry.Kind.any, limit);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findDirectories(final String fromPath) throws CacheException {
    return entryCacheManager.getCache("defaultsvn").findByPattern(fromPath + ".*?", dir, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<LogMessage> find(final String queryString) throws CacheException {
    return logMessageCacheManager.getCache("defaultsvn").find(queryString);
  }

  /**
   * {@inheritDoc}
   */
  public SVNLogEntry getRevision(final long revision) throws CacheException {
    final RevisionCache cache = revisionCacheManager.getCache("defaultsvn");
    return cache.get(revision);
  }

}
