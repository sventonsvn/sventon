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
package org.sventon.cache.entrycache;

import org.sventon.cache.CacheException;
import org.sventon.cache.CacheManager;
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
  private final File rootDirectory;

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
   * @param repositoryName Name of cache instance.
   * @return The created cache instance.
   * @throws CacheException if unable to create cache.
   */
  protected EntryCache createCache(final RepositoryName repositoryName) throws CacheException {
    logger.debug("Creating cache: " + repositoryName);
    return new DiskCache(new File(new File(rootDirectory, repositoryName.toString()), "cache"));
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
