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

import org.sventon.appl.ConfigDirectory;
import org.sventon.cache.objectcache.ObjectCache;
import org.sventon.cache.objectcache.ObjectCacheImpl;
import org.sventon.model.RepositoryName;

import javax.annotation.PreDestroy;
import javax.management.MBeanServer;
import java.io.File;

/**
 * Handles EntryCache instances.
 *
 * @author jesper@sventon.org
 */
public class ObjectCacheManager extends CacheManager<ObjectCache> {

  /**
   * Root directory for cache files.
   */
  private final File repositoriesDirectory;

  private final int maxElementsInMemory;
  private final boolean overflowToDisk;
  private final boolean eternal;
  private final int timeToLiveSeconds;
  private final int timeToIdleSeconds;
  private final boolean diskPersistent;
  private final int diskExpiryThreadIntervalSeconds;
  private MBeanServer mBeanServer;

  /**
   * Constructor.
   *
   * @param configDirectory     Configuration directory.
   *                            Cache files will be stored in a sub dir called <tt>cache</tt>.
   * @param maxElementsInMemory Max elements in memory
   * @param overflowToDisk      Overflow to disk
   * @param eternal             If true, objects never expire
   * @param timeToLiveSeconds   Object time to live in seconds
   * @param timeToIdleSeconds   Object time to idle in seconds
   * @param diskPersistent      If true, cache will be stored on disk
   * @param diskExpiryThreadIntervalSeconds
   *                            Expiry thread interval
   */
  public ObjectCacheManager(final ConfigDirectory configDirectory,
                            final int maxElementsInMemory,
                            final boolean overflowToDisk,
                            final boolean eternal,
                            final int timeToLiveSeconds,
                            final int timeToIdleSeconds,
                            final boolean diskPersistent,
                            final int diskExpiryThreadIntervalSeconds) {
    this.repositoriesDirectory = configDirectory.getRepositoriesDirectory();
    this.maxElementsInMemory = maxElementsInMemory;
    this.overflowToDisk = overflowToDisk;
    this.eternal = eternal;
    this.timeToLiveSeconds = timeToLiveSeconds;
    this.timeToIdleSeconds = timeToIdleSeconds;
    this.diskPersistent = diskPersistent;
    this.diskExpiryThreadIntervalSeconds = diskExpiryThreadIntervalSeconds;
  }

  /**
   * @param mBeanServer mBeanServer instance.
   */
  public void setMBeanServer(final MBeanServer mBeanServer) {
    this.mBeanServer = mBeanServer;
  }

  /**
   * Creates a new cache instance using given name and default settings.
   *
   * @param repositoryName Name of cache instance.
   * @return The created cache instance.
   * @throws org.sventon.cache.CacheException
   *          if unable to create cache.
   */
  @Override
  protected ObjectCache createCache(final RepositoryName repositoryName) throws CacheException {
    final File cachePath = new File(new File(repositoriesDirectory, repositoryName.toString()), "cache");
    logger.debug("Creating cache: " + cachePath.getAbsolutePath());

    if (!cachePath.exists() && !cachePath.mkdirs()) {
      throw new CacheException("Unable to create directory: " + cachePath.getAbsolutePath());
    }

    final ObjectCacheImpl objectCache = new ObjectCacheImpl(repositoryName.toString(), cachePath.getAbsolutePath(),
        maxElementsInMemory, overflowToDisk, eternal, timeToLiveSeconds, timeToIdleSeconds, diskPersistent,
        diskExpiryThreadIntervalSeconds);

    objectCache.flush();
    objectCache.setMBeanServer(mBeanServer);
    objectCache.init();
    return objectCache;
  }

  /**
   * Shuts all the caches down.
   *
   * @throws CacheException if unable to shutdown caches.
   */
  @PreDestroy
  @Override
  public void shutdown() throws CacheException {
    for (final ObjectCache cache : caches.values()) {
      cache.shutdown();
    }
  }

  @Override
  public void shutdown(RepositoryName repositoryName) throws CacheException {
    ObjectCache cache = getCache(repositoryName);
    if (cache != null) {
      cache.shutdown();
    }
  }
}
