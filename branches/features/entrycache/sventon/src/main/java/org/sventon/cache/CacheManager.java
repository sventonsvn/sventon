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
package org.sventon.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.model.RepositoryName;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract CacheManager class. To be subclasses by the cache specific managers.
 *
 * @author jesper@sventon.org
 */
public abstract class CacheManager<T> {

  /**
   * Logger for this class and subclasses.
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * The cache instances.
   */
  protected final Map<RepositoryName, T> caches = new HashMap<RepositoryName, T>();

  /**
   * Gets a cache by name. If cache does not exist yet, it will be created
   * using the default settings.
   *
   * @param repositoryName Name of cache to get
   * @return The cache instance.
   * @throws CacheException if cache does not exist.
   */
  public final T getCache(final RepositoryName repositoryName) throws CacheException {
    T cache = caches.get(repositoryName);
    if (cache == null) {
      throw new CacheException("Unknown cache name: " + repositoryName);
    }
    return cache;
  }

  /**
   * Registers the repository in the cache manager.
   * The repository cache will be created if it does not exist.
   *
   * @param repositoryName Repository name.
   * @throws CacheException if unable to create cache instance, or if the
   *                        cache has already been registered.
   */
  public final void register(final RepositoryName repositoryName) throws CacheException {
    T cache = caches.get(repositoryName);
    if (cache != null) {
      throw new IllegalStateException("Cache [" + repositoryName + "] has already been registered");
    }
    cache = createCache(repositoryName);
    logger.debug("Adding cache, type [" + cache.getClass().getName() + "] name, [" + repositoryName + "]");
    addCache(repositoryName, cache);
  }

  /**
   * Checks if given repository name is registered.
   *
   * @param repositoryName Name
   * @return True if registered, false if not.
   */
  public boolean isRegistered(final RepositoryName repositoryName) {
    return caches.get(repositoryName) != null;
  }

  /**
   * Creates a new cache instance using given name and default settings.
   *
   * @param repositoryName Name of cache instance.
   * @return The created cache instance.
   * @throws CacheException if unable to create cache.
   */
  protected abstract T createCache(final RepositoryName repositoryName) throws CacheException;

  /**
   * For test purposes only.
   * Adds a cache instance to the manager's list.
   *
   * @param repositoryName Name of cache
   * @param cache          Cache instance
   * @return The added cache instance
   */
  public final T addCache(final RepositoryName repositoryName, final T cache) {
    caches.put(repositoryName, cache);
    return cache;
  }

}
