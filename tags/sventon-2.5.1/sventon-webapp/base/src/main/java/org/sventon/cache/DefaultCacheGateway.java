/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.sventon.cache.direntrycache.DirEntryCache;
import org.sventon.cache.logmessagecache.LogMessageCache;
import org.sventon.model.CamelCasePattern;
import org.sventon.model.DirEntry;
import org.sventon.model.LogMessageSearchItem;
import org.sventon.model.RepositoryName;

import java.util.List;

/**
 * Gateway class used to access the caches.
 *
 * @author jesper@sventon.org
 */
public final class DefaultCacheGateway implements CacheGateway {

  private DirEntryCacheManager cacheManager;
  private LogMessageCacheManager logMessageCacheManager;

  /**
   * Sets the entry cache manager instance.
   *
   * @param cacheManager DirEntryCacheManager instance.
   */
  @Autowired
  public void setEntryCacheManager(final DirEntryCacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  /**
   * Sets the log entry cache manager instance.
   *
   * @param logMessageCacheManager LogMessageCacheManager instance.
   */
  @Autowired
  public void setLogEntryCacheManager(final LogMessageCacheManager logMessageCacheManager) {
    this.logMessageCacheManager = logMessageCacheManager;
  }

  @Override
  public List<DirEntry> findEntries(final RepositoryName repositoryName, final String searchString,
                                    final String startDir)
      throws CacheException {
    final DirEntryCache entryCache = cacheManager.getCache(repositoryName);
    assertCacheExists(entryCache, repositoryName);
    return entryCache.findEntries(searchString, startDir);
  }

  @Override
  public List<DirEntry> findEntriesByCamelCase(final RepositoryName repositoryName, final CamelCasePattern pattern,
                                               final String startDir) throws CacheException {
    final DirEntryCache entryCache = cacheManager.getCache(repositoryName);
    assertCacheExists(entryCache, repositoryName);
    return entryCache.findEntriesByCamelCasePattern(pattern, startDir);
  }

  @Override
  public List<DirEntry> findDirectories(final RepositoryName repositoryName, final String fromPath) throws CacheException {
    final DirEntryCache entryCache = cacheManager.getCache(repositoryName);
    assertCacheExists(entryCache, repositoryName);
    return entryCache.findDirectories(fromPath);
  }

  @Override
  public List<LogMessageSearchItem> find(final RepositoryName repositoryName, final String queryString) throws CacheException {
    final LogMessageCache cache = logMessageCacheManager.getCache(repositoryName);
    assertCacheExists(cache, repositoryName);
    return cache.find(queryString);
  }

  @Override
  public List<LogMessageSearchItem> find(final RepositoryName repositoryName, final String queryString, final String startDir) throws CacheException {
    final LogMessageCache cache = logMessageCacheManager.getCache(repositoryName);
    assertCacheExists(cache, repositoryName);
    return cache.find(queryString, startDir);
  }

  private void assertCacheExists(final Cache cache, final RepositoryName repositoryName) throws CacheException {
    if (cache == null) {
      throw new CacheException("There is no cache repository named [" + repositoryName + "]");
    }
  }
}
