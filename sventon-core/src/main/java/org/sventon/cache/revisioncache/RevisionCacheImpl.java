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
package org.sventon.cache.revisioncache;

import org.apache.commons.lang.Validate;
import org.sventon.cache.objectcache.ObjectCache;
import org.sventon.model.LogEntry;

/**
 * Contains cached revisions.
 *
 * @author jesper@sventon.org
 */
public final class RevisionCacheImpl implements RevisionCache {

  /**
   * Object cache instance.
   */
  private final ObjectCache objectCache;

  /**
   * Constructor.
   *
   * @param objectCache Object cache instance.
   */
  public RevisionCacheImpl(final ObjectCache objectCache) {
    this.objectCache = objectCache;
  }

  @Override
  public LogEntry get(final long revision) {
    return (LogEntry) objectCache.get(CacheKey.createKey(revision));
  }

  @Override
  public void add(final LogEntry logEntry) {
    Validate.notNull(logEntry, "Given logEntry was null");
    objectCache.put(CacheKey.createKey(logEntry.getRevision()), logEntry);
  }

  @Override
  public void flush() {
    objectCache.flush();
  }

  /**
   * Class used as revision cache key.
   */
  private static class CacheKey {

    /**
     * Private.
     */
    private CacheKey() {
    }

    /**
     * Creates a key string.
     *
     * @param revision Revision.
     * @return Key
     */
    private static String createKey(final long revision) {
      return "svnRevision-" + revision;
    }
  }

}
