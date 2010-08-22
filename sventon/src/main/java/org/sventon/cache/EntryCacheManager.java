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
import org.sventon.cache.direntrycache.CompassDirEntryCache;
import org.sventon.cache.direntrycache.DirEntryCache;
import org.sventon.model.RepositoryName;

import javax.annotation.PreDestroy;
import java.io.File;

/**
 * Handles DirEntryCache instances.
 *
 * @author jesper@sventon.org
 */
public final class EntryCacheManager extends CacheManager<DirEntryCache> {

  /**
   * Root directory for cache files.
   */
  private final File repositoriesDirectory;

  /**
   * Constructor.
   *
   * @param configDirectory Directory where to store cache files.
   */
  public EntryCacheManager(final ConfigDirectory configDirectory) {
    logger.debug("Starting cache manager. Using [" + configDirectory.getRepositoriesDirectory() + "] as root directory");
    this.repositoriesDirectory = configDirectory.getRepositoriesDirectory();
  }

  /**
   * Creates a new cache instance using given name and default settings.
   *
   * @param repositoryName Name of cache instance.
   * @return The created cache instance.
   * @throws CacheException if unable to create cache.
   */
  @Override
  protected DirEntryCache createCache(final RepositoryName repositoryName) throws CacheException {
    logger.debug("Creating cache: " + repositoryName);
    final File cacheDirectory = new File(new File(repositoriesDirectory, repositoryName.toString()), "cache");
    logger.debug("Using dir: " + cacheDirectory.getAbsolutePath());
    final DirEntryCache entryCache = new CompassDirEntryCache(cacheDirectory, true);
    entryCache.init();
    return entryCache;
  }


  /**
   * Shuts all the caches down.
   *
   * @throws CacheException if unable to shutdown caches.
   */
  @PreDestroy
  @Override
  public void shutdown() throws CacheException {
    for (final DirEntryCache entryCache : caches.values()) {
      entryCache.shutdown();
    }
  }
}
