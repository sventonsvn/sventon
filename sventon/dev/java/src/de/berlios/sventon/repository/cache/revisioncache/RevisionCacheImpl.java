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
package de.berlios.sventon.repository.cache.revisioncache;

import org.tmatesoft.svn.core.SVNLogEntry;
import de.berlios.sventon.repository.cache.CacheException;
import de.berlios.sventon.repository.cache.objectcache.ObjectCache;

/**
 * Contains cached revisions.
 *
 * @author jesper@users.berlios.de
 */
public class RevisionCacheImpl implements RevisionCache {

  /**
   * Object cache instance.
   */
  private final ObjectCache objectCache;

  public RevisionCacheImpl(final ObjectCache objectCache) {
    this.objectCache = objectCache;
  }

  /**
   * {@inheritDoc}
   */
  public SVNLogEntry get(final long revision) throws CacheException {
    return (SVNLogEntry) objectCache.get(CacheKey.createKey(revision));
  }

  /**
   * {@inheritDoc}
   */
  public void add(final SVNLogEntry logEntry) throws CacheException {
    if (logEntry == null) {
      throw new IllegalArgumentException("Given logEntry was null");
    }
    objectCache.put(CacheKey.createKey(logEntry.getRevision()), logEntry);
  }

  /**
   * Class used as revision cache key.
   */
  private static class CacheKey {
    private static String createKey(final long revision) {
      return "svnRevision-" + revision;
    }
  }

}
