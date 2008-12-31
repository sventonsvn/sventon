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
package org.sventon.cache.revisioncache;

import org.sventon.cache.CacheException;
import org.sventon.cache.CacheManager;
import org.sventon.cache.objectcache.ObjectCacheManager;
import org.sventon.model.RepositoryName;

/**
 * Handles RevisionCache instances.
 *
 * @author jesper@sventon.org
 */
public final class RevisionCacheManager extends CacheManager<RevisionCache> {

  /**
   * Object cache manager instance.
   */
  private final ObjectCacheManager objectCacheManager;

  /**
   * Constructor.
   *
   * @param objectCacheManager ObjectCacheManager instance.
   */
  public RevisionCacheManager(final ObjectCacheManager objectCacheManager) {
    logger.debug("Starting cache manager");
    this.objectCacheManager = objectCacheManager;
  }

  /**
   * Creates a new cache instance using given name and default settings.
   *
   * @param repositoryName Name of cache instance.
   * @return The created cache instance.
   * @throws CacheException if unable to create cache.
   */
  protected RevisionCache createCache(final RepositoryName repositoryName) throws CacheException {
    logger.debug("Creating cache: " + repositoryName);
    return new RevisionCacheImpl(objectCacheManager.getCache(repositoryName));
  }

}
