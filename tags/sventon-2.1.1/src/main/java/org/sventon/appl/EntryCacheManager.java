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
package org.sventon.appl;

import org.sventon.cache.CacheException;
import org.sventon.cache.entrycache.EntryCache;
import org.sventon.cache.entrycache.EntryCacheImpl;
import org.sventon.model.RepositoryName;

import java.io.File;

/**
 * Handles EntryCache instances.
 *
 * @author jesper@sventon.org
 */
public final class EntryCacheManager extends CacheManager<EntryCache> {

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
  protected EntryCache createCache(final RepositoryName repositoryName) throws CacheException {
    logger.debug("Creating cache: " + repositoryName);
    final File cacheDirectory = new File(new File(repositoriesDirectory, repositoryName.toString()), "cache");
    logger.debug("Using dir: " + cacheDirectory.getAbsolutePath());
    final EntryCache cache = new EntryCacheImpl(cacheDirectory, true);
    cache.init();
    return cache;
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
