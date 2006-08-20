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

import de.berlios.sventon.repository.cache.objectcache.ObjectCacheManager;
import de.berlios.sventon.repository.cache.CacheException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles RevisionCache instances.
 *
 * @author jesper@users.berlios.de
 */
public class RevisionCacheManager {

  /**
   * Logger for this class and subclasses
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * The cache instances.
   */
  final Map<String, RevisionCache> caches = new HashMap<String, RevisionCache>();

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
   * Gets a cache by name. If cache does not exist yet, it will be created
   * using the default settings.
   *
   * @param cacheName Name of cache to get
   * @return The cache instance.
   */
  public RevisionCache getCache(final String cacheName) throws CacheException {
    logger.debug("Getting cache: " + cacheName);
    RevisionCache cache = caches.get(cacheName);
    if (cache == null) {
      cache = addCache(cacheName, createCache(cacheName));
    }
    return cache;
  }

  /**
   * Creates a new cache instance using given name and default settings.
   *
   * @param cacheName Name of cache instance.
   * @return The created cache instance.
   * @throws CacheException if unable to create cache.
   */
  private RevisionCache createCache(final String cacheName) throws CacheException {
    logger.debug("Creating cache: " + cacheName);
    return new RevisionCacheImpl(objectCacheManager.getCache(cacheName));
  }

  /**
   * For test purposes only.
   * Adds a cache instance to the manager's list.
   *
   * @param cacheName  Name of cache
   * @param revisionCache Cache instance
   * @return The added cache instance
   */
  public RevisionCache addCache(final String cacheName, final RevisionCache revisionCache) {
    return caches.put(cacheName, revisionCache);
  }

  /**
   * Gets all cache instances.
   *
   * @return Cache instances.
   */
  public Map<String, RevisionCache> getCaches() {
    return Collections.unmodifiableMap(caches);
  }

}
