/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.repository.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract CacheManager class. To be subclasses by the cache specific managers.
 *
 * @author jesper@users.berlios.de
 */
public abstract class CacheManager<T> {

  /**
   * Logger for this class and subclasses
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * The cache instances.
   */
  protected final Map<String, T> caches = new HashMap<String, T>();

  /**
   * Gets a cache by name. If cache does not exist yet, it will be created
   * using the default settings.
   *
   * @param cacheName Name of cache to get
   * @return The cache instance.
   * @throws CacheException if cache does not exist.
   */
  public final T getCache(final String cacheName) throws CacheException {
    T cache = caches.get(cacheName);
    if (cache == null) {
      throw new CacheException("Unknown cache instance name: " + cacheName);
    }
    return cache;
  }

  /**
   * Registers the instance in the cache manager.
   * The instance cache will be created if it does not exist.
   *
   * @param cacheName Instance name.
   * @throws CacheException if unable to create cache instance, or if the
   *                        cache has already been registered.
   */
  public final void register(final String cacheName) throws CacheException {
    T cache = caches.get(cacheName);
    if (cache != null) {
      throw new IllegalStateException("Cache instance [" + cacheName + "] has already been registered");
    }
    addCache(cacheName, createCache(cacheName));
  }

  /**
   * Creates a new cache instance using given name and default settings.
   *
   * @param cacheName Name of cache instance.
   * @return The created cache instance.
   * @throws CacheException if unable to create cache.
   */
  protected abstract T createCache(final String cacheName) throws CacheException;

  /**
   * For test purposes only.
   * Adds a cache instance to the manager's list.
   *
   * @param cacheName Name of cache
   * @param cache     Cache instance
   * @return The added cache instance
   */
  public final T addCache(final String cacheName, final T cache) {
    caches.put(cacheName, cache);
    return cache;
  }

}
