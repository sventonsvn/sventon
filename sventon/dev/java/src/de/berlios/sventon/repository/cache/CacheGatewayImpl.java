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
package de.berlios.sventon.repository.cache;

import de.berlios.sventon.repository.LogMessage;
import de.berlios.sventon.repository.RepositoryEntry;
import static de.berlios.sventon.repository.RepositoryEntry.Kind.dir;
import de.berlios.sventon.repository.cache.entrycache.EntryCache;
import de.berlios.sventon.repository.cache.entrycache.EntryCacheManager;
import de.berlios.sventon.repository.cache.logmessagecache.LogMessageCache;
import de.berlios.sventon.repository.cache.logmessagecache.LogMessageCacheManager;
import de.berlios.sventon.repository.cache.revisioncache.RevisionCache;
import de.berlios.sventon.repository.cache.revisioncache.RevisionCacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Gateway class used to access the caches.
 *
 * @author jesper@users.berlios.de
 */
public class CacheGatewayImpl implements CacheGateway {

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
  public CacheGatewayImpl() {
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
  public void setRevisionCacheManager(final RevisionCacheManager revisionCacheManager) {
    this.revisionCacheManager = revisionCacheManager;
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String instanceName, final String searchString) throws CacheException {
    final EntryCache cache = entryCacheManager.getCache(instanceName);
    assertCacheExists(cache, instanceName);
    return cache.findByPattern(Pattern.compile("/" + ".*?" + searchString + ".*?", Pattern.CASE_INSENSITIVE), RepositoryEntry.Kind.any, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntryByCamelCase(final String instanceName, final CamelCasePattern pattern, final String startDir) throws CacheException {
    final EntryCache cache = entryCacheManager.getCache(instanceName);
    assertCacheExists(cache, instanceName);
    String rootDir = startDir;
    if (rootDir.endsWith("/")) {
      rootDir = rootDir.substring(0, rootDir.length() - 1);
    }
    return cache.findByPattern(Pattern.compile(".*" + rootDir + ".*?[/]" + pattern.getPattern()), RepositoryEntry.Kind.any, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String instanceName, final String searchString, final String startDir) throws CacheException {
    final EntryCache cache = entryCacheManager.getCache(instanceName);
    assertCacheExists(cache, instanceName);
    return cache.findByPattern(Pattern.compile(startDir + ".*?" + searchString + ".*?", Pattern.CASE_INSENSITIVE), RepositoryEntry.Kind.any, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String instanceName, final String searchString, final String startDir, final Integer limit) throws CacheException {
    final EntryCache cache = entryCacheManager.getCache(instanceName);
    assertCacheExists(cache, instanceName);
    return cache.findByPattern(Pattern.compile(startDir + ".*?" + searchString + ".*?", Pattern.CASE_INSENSITIVE), RepositoryEntry.Kind.any, limit);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findDirectories(final String instanceName, final String fromPath) throws CacheException {
    final EntryCache cache = entryCacheManager.getCache(instanceName);
    assertCacheExists(cache, instanceName);
    return cache.findByPattern(Pattern.compile(fromPath + ".*?", Pattern.CASE_INSENSITIVE), dir, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<LogMessage> find(final String instanceName, final String queryString) throws CacheException {
    final LogMessageCache cache = logMessageCacheManager.getCache(instanceName);
    assertCacheExists(cache, instanceName);
    return cache.find(queryString);
  }

  /**
   * {@inheritDoc}
   */
  public SVNLogEntry getRevision(final String instanceName, final long revision) throws CacheException {
    final RevisionCache cache = revisionCacheManager.getCache(instanceName);
    assertCacheExists(cache, instanceName);
    return cache.get(revision);
  }

  private void assertCacheExists(final Cache cache, final String instanceName) throws CacheException {
    if (cache == null) {
      throw new CacheException("There is no cache instance named [" + instanceName + "]");
    }
  }
}
