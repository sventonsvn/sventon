/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.repository.cache.objectcache;

import de.berlios.sventon.repository.cache.CacheException;
import de.berlios.sventon.repository.cache.CacheManager;

import java.io.File;

/**
 * Handles EntryCache instances.
 *
 * @author jesper@users.berlios.de
 */
public final class ObjectCacheManager extends CacheManager<ObjectCache> {

  /**
   * Root directory for cache files.
   */
  private final File rootDirectory;
  private final int maxElementsInMemory;
  private final boolean overflowToDisk;
  private final boolean eternal;
  private final int timeToLiveSeconds;
  private final int timeToIdleSeconds;
  private final boolean diskPersistent;
  private final int diskExpiryThreadIntervalSeconds;

  /**
   * Constructor.
   *
   * @param rootDirectory       Root directory for cache files.
   * @param maxElementsInMemory Max elements in memory
   * @param overflowToDisk      Overflow to disk
   * @param eternal             If true, objects never expire
   * @param timeToLiveSeconds   Object time to live in seconds
   * @param timeToIdleSeconds   Object time to idle in seconds
   * @param diskPersistent      If true, cache will be stored on disk
   * @param diskExpiryThreadIntervalSeconds
   *                            Expiry thread interval
   */
  public ObjectCacheManager(final File rootDirectory,
                            final int maxElementsInMemory,
                            final boolean overflowToDisk,
                            final boolean eternal,
                            final int timeToLiveSeconds,
                            final int timeToIdleSeconds,
                            final boolean diskPersistent,
                            final int diskExpiryThreadIntervalSeconds) {
    this.rootDirectory = rootDirectory;
    this.maxElementsInMemory = maxElementsInMemory;
    this.overflowToDisk = overflowToDisk;
    this.eternal = eternal;
    this.timeToLiveSeconds = timeToLiveSeconds;
    this.timeToIdleSeconds = timeToIdleSeconds;
    this.diskPersistent = diskPersistent;
    this.diskExpiryThreadIntervalSeconds = diskExpiryThreadIntervalSeconds;
  }

  /**
   * Creates a new cache instance using given name and default settings.
   *
   * @param cacheName Name of cache instance.
   * @return The created cache instance.
   * @throws de.berlios.sventon.repository.cache.CacheException
   *          if unable to create cache.
   */
  protected ObjectCache createCache(final String cacheName) throws CacheException {
    logger.debug("Creating cache: " + cacheName);
    final File cachePath = new File(rootDirectory, cacheName);
    cachePath.mkdirs();
    return new ObjectCacheImpl(
        cacheName,
        cachePath.getAbsolutePath(),
        maxElementsInMemory,
        overflowToDisk,
        eternal,
        timeToLiveSeconds,
        timeToIdleSeconds,
        diskPersistent,
        diskExpiryThreadIntervalSeconds);
  }

  /**
   * Shuts all the caches down.
   *
   * @throws CacheException if unable to shutdown caches.
   */
  public void shutdown() throws CacheException {
    for (final ObjectCache cache : caches.values()) {
      cache.shutdown();
    }
  }
}
