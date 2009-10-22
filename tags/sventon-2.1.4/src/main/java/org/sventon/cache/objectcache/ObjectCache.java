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
package org.sventon.cache.objectcache;

import org.sventon.cache.Cache;
import org.sventon.cache.CacheException;

/**
 * Interface to be implemented by sventon object cache class.
 *
 * @author jesper@user.berlios.de
 */
public interface ObjectCache extends Cache {

  /**
   * Puts an object into the cache.
   *
   * @param cacheKey The cache cacheKey.
   * @param value    The object to cache.
   */
  void put(final Object cacheKey, final Object value);

  /**
   * Gets an object from the cache.
   *
   * @param cacheKey The key to the object to get.
   * @return The cached object. <code>null</code> if cache miss.
   */
  Object get(final Object cacheKey);

  /**
   * Flushes the cache to disk. Only applies to <tt>DiskStore</tt> implementations.
   */
  void flush();

  /**
   * Shuts the cache down.
   *
   * @throws CacheException if unable to shutdown cache.
   */
  void shutdown() throws CacheException;
}
