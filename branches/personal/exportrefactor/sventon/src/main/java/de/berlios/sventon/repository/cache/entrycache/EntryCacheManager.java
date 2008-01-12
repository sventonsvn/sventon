/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
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
import de.berlios.sventon.repository.cache.CacheManager;

import java.io.File;

/**
 * Handles EntryCache instances.
 *
 * @author jesper@users.berlios.de
 */
public final class EntryCacheManager extends CacheManager<EntryCache> {

  /**
   * Root directory for cache files.
   */
  private File rootDirectory;

  /**
   * Constructor.
   *
   * @param rootDirectory Directory where to store cache files.
   */
  public EntryCacheManager(final File rootDirectory) {
    logger.debug("Starting cache manager. Using [" + rootDirectory + "] as root directory");
    this.rootDirectory = rootDirectory;
  }

  /**
   * Creates a new cache instance using given name and default settings.
   *
   * @param cacheName Name of cache instance.
   * @return The created cache instance.
   * @throws CacheException if unable to create cache.
   */
  protected EntryCache createCache(final String cacheName) throws CacheException {
    logger.debug("Creating cache: " + cacheName);
    return new DiskCache(new File(rootDirectory, cacheName));
  }


  /**
   * Shuts all the caches down.
   *
   * @throws CacheException if unable to shutdown caches.
   */
  public void shutdown() throws CacheException {
    for (final EntryCache cache : caches.values()) {
      cache.shutdown();
    }
  }
}
