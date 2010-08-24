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
package org.sventon.cache;

import org.sventon.cache.revisioncache.RevisionCache;
import org.sventon.cache.revisioncache.RevisionCacheImpl;
import org.sventon.model.RepositoryName;

import javax.annotation.PreDestroy;

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
  @Override
  protected RevisionCache createCache(final RepositoryName repositoryName) throws CacheException {
    logger.debug("Creating cache: " + repositoryName);
    return new RevisionCacheImpl(objectCacheManager.getCache(repositoryName));
  }

  @Override
  @PreDestroy
  public void shutdown() throws CacheException {
    objectCacheManager.shutdown();
  }

  @Override
  public void shutdown(RepositoryName repositoryName) throws CacheException {
    objectCacheManager.shutdown(repositoryName);
  }

}
