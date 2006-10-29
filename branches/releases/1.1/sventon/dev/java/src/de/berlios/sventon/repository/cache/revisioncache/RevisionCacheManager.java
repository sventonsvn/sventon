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

import de.berlios.sventon.repository.cache.CacheException;
import de.berlios.sventon.repository.cache.CacheManager;
import de.berlios.sventon.repository.cache.objectcache.ObjectCacheManager;

/**
 * Handles RevisionCache instances.
 *
 * @author jesper@users.berlios.de
 */
public class RevisionCacheManager extends CacheManager<RevisionCache> {

  /**
   * Root directory for cache files.
   */
  private String rootDirectory;

  /**
   * Object cache manager instance.
   */
  private ObjectCacheManager objectCacheManager;

  /**
   * Constructor.
   *
   * @param rootDirectory Directory where to store cache files.
   */
  public RevisionCacheManager(final String rootDirectory, final ObjectCacheManager objectCacheManager) {
    logger.debug("Starting cache manager. Using [" + rootDirectory + "] as root directory");
    this.rootDirectory = rootDirectory;
    this.objectCacheManager = objectCacheManager;
  }

  /**
   * Creates a new cache instance using given name and default settings.
   *
   * @param cacheName Name of cache instance.
   * @return The created cache instance.
   * @throws CacheException if unable to create cache.
   */
  protected RevisionCache createCache(final String cacheName) throws CacheException {
    logger.debug("Creating cache: " + cacheName);
    return new RevisionCacheImpl(objectCacheManager.getCache(cacheName));
  }

}
