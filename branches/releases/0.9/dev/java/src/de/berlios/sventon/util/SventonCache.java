/*
 * ====================================================================
 * Copyright (c) 2005 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

/**
 * Wrapper class for cache handling.
 *
 * @author jesper@users.berlios.de
 */
public class SventonCache {

  /** The cache instance. */
  private Cache cache = null;

  /** Logger for this class and subclasses */
  protected final Log logger = LogFactory.getLog(getClass());

  /** Sventon cache name. */
  public static final String CACHE_NAME = "sventonCache";

  /** Singelton instance of the factory. */
  public static final SventonCache INSTANCE = new SventonCache();

  /**
   * Private contructor.
   * As this is a singleton instance it must not
   * be extended or created from the outside.
   * @throws RuntimeException if unable to create cache instance.
   */
  private SventonCache() {
    try {
      init();
    } catch(CacheException ce) {
      throw new RuntimeException("Unable to create cache instance");
    }
  }

  /**
   * Initializes the cache.
   * @throws CacheException if cache initialization error occur.
   */
  private synchronized void init() throws CacheException {
    CacheManager cacheManager = CacheManager.getInstance();
    cache = cacheManager.getCache(CACHE_NAME);
  }

  /**
   * Puts an object into the cache.
   * @param cacheKey The cache cacheKey - <code>toString</code> will be executed on the cacheKey.
   * @param value The object to cache.
   * @throws CacheException if unable to cache object.
   * @throws IllegalArgumentException if cache cacheKey is null.
   */
  public void put(final Object cacheKey, final Object value) throws CacheException {
    if (cacheKey == null) {
      throw new IllegalArgumentException("Cachekey cannot be null");
    }
    Element element = new Element(cacheKey.toString(), (Serializable) value);
    cache.put(element);
  }

  /**
   * Gets an object from the cache.
   * @param key The key to the object to get.
   * @return The cached object. <code>null</code> if cache miss.
   * @throws CacheException if unable to get object from cache.
   */
  public Object get(final Object key) throws CacheException {
    String cacheKey = key.toString();
    Element element = cache.get(cacheKey);
    return element != null ? element.getValue() : null;
  }

  /**
   * @return Current memory size of the cache.
   * @throws CacheException if unable to get memory size.
   */
  public long getMemorySize() throws CacheException {
    return cache.calculateInMemorySize();
  }

  /**
   * @return The cache hit count.
   * @throws CacheException if unable to get cache hit count.
   */
  public long getHitCount() throws CacheException {
    return cache.getHitCount();
  }

}
