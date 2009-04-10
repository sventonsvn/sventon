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
package org.sventon.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.appl.EntryCacheManager;
import org.sventon.appl.LogMessageCacheManager;
import org.sventon.appl.RevisionCacheManager;
import org.sventon.cache.entrycache.EntryCache;
import org.sventon.cache.logmessagecache.LogMessageCache;
import org.sventon.cache.revisioncache.RevisionCache;
import org.sventon.model.CamelCasePattern;
import org.sventon.model.LogMessage;
import org.sventon.model.RepositoryEntry;
import org.sventon.model.RepositoryName;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Gateway class used to access the caches.
 *
 * @author jesper@sventon.org
 */
public final class CacheGatewayImpl implements CacheGateway {

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
  public List<RepositoryEntry> findEntries(final RepositoryName repositoryName, final String searchString,
                                           final String startDir, final boolean includeAuthors)
      throws CacheException {
    final EntryCache cache = entryCacheManager.getCache(repositoryName);
    assertCacheExists(cache, repositoryName);
    return cache.findEntries(searchString, startDir);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntriesByCamelCase(final RepositoryName repositoryName, final CamelCasePattern pattern,
                                                      final String startDir) throws CacheException {
    final EntryCache cache = entryCacheManager.getCache(repositoryName);
    assertCacheExists(cache, repositoryName);
    return cache.findEntriesByCamelCasePattern(pattern, startDir);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findDirectories(final RepositoryName repositoryName, final String fromPath) throws CacheException {
    final EntryCache cache = entryCacheManager.getCache(repositoryName);
    assertCacheExists(cache, repositoryName);
    return cache.findDirectories(fromPath);
  }

  /**
   * {@inheritDoc}
   */
  public List<LogMessage> find(final RepositoryName repositoryName, final String queryString) throws CacheException {
    final LogMessageCache cache = logMessageCacheManager.getCache(repositoryName);
    assertCacheExists(cache, repositoryName);
    return cache.find(queryString);
  }

  /**
   * {@inheritDoc}
   */
  public SVNLogEntry getRevision(final RepositoryName repositoryName, final long revision) throws CacheException {
    final RevisionCache cache = revisionCacheManager.getCache(repositoryName);
    assertCacheExists(cache, repositoryName);
    return cache.get(revision);
  }

  /**
   * {@inheritDoc}
   */
  public List<SVNLogEntry> getRevisions(final RepositoryName repositoryName, final List<Long> revisions) throws CacheException {
    final RevisionCache cache = revisionCacheManager.getCache(repositoryName);
    assertCacheExists(cache, repositoryName);
    final List<SVNLogEntry> cachedRevisions = new ArrayList<SVNLogEntry>();
    for (final Long revision : revisions) {
      cachedRevisions.add(cache.get(revision));
    }
    return cachedRevisions;
  }

  private void assertCacheExists(final Cache cache, final RepositoryName repositoryName) throws CacheException {
    if (cache == null) {
      throw new CacheException("There is no cache repository named [" + repositoryName + "]");
    }
  }
}
