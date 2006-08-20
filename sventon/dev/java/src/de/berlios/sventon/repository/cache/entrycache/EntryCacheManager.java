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
package de.berlios.sventon.repository.cache.entrycache;

import de.berlios.sventon.repository.cache.CacheException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles EntryCache instances.
 *
 * @author jesper@users.berlios.de
 */
public class EntryCacheManager {

  /**
   * Logger for this class and subclasses
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * The cache instances.
   */
  final Map<String, EntryCache> caches = new HashMap<String, EntryCache>();

  /**
   * Root directory for cache files.
   */
  private String rootDirectory;

  /**
   * Constructor.
   *
   * @param rootDirectory Directory where to store cache files.
   */
  public EntryCacheManager(final String rootDirectory) {
    logger.debug("Starting cache manager. Using [" + rootDirectory + "] as root directory");
    this.rootDirectory = rootDirectory;
  }

  /**
   * Gets a cache by name. If cache does not exist yet, it will be created
   * using the default settings.
   *
   * @param cacheName Name of cache to get
   * @return The cache instance.
   */
  public EntryCache getCache(final String cacheName) throws CacheException {
    logger.debug("Getting cache: " + cacheName);
    EntryCache entryCache = caches.get(cacheName);
    if (entryCache == null) {
      entryCache = addCache(cacheName, createCache(cacheName));
    }
    return entryCache;
  }

  /**
   * Creates a new cache instance using given name and default settings.
   *
   * @param cacheName Name of cache instance.
   * @return The created cache instance.
   * @throws CacheException if unable to create cache.
   */
  private EntryCache createCache(final String cacheName) throws CacheException {
    logger.debug("Creating cache: " + cacheName);
    return new DiskCache(rootDirectory);
  }

  /**
   * For test purposes only.
   * Adds a cache instance to the manager's list.
   *
   * @param cacheName  Name of cache
   * @param entryCache Cache instance
   * @return The added cache instance
   */
  public EntryCache addCache(final String cacheName, final EntryCache entryCache) {
    logger.debug("Adding cache: " + cacheName);
    return caches.put(cacheName, entryCache);
  }

  /**
   * Gets all cache instances.
   *
   * @return Cache instances.
   */
  public Map<String, EntryCache> getCaches() {
    return Collections.unmodifiableMap(caches);
  }

  /**
   * Shuts all the caches down.
   *
   * @throws CacheException if unable to shutdown caches.
   */
  public void shutdown() throws CacheException {
    for (final EntryCache entryCache : caches.values()) {
      entryCache.shutdown();
    }
  }
}
