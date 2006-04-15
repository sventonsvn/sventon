/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.CacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

/**
 * Wrapper class for cache handling. Uses
 * <a href="http://ehcache.sourceforge.net/">ehcache</a> behind the scenes.
 *
 * @author jesper@users.berlios.de
 */
public final class SventonCacheImpl implements SventonCache {

  /**
   * The cache instance.
   */
  private Cache cache = null;

  /**
   * Logger for this class and subclasses
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * Sventon cache name.
   */
  public static final String CACHE_NAME = "sventonCache";

  /**
   * Constructs the cache instance.
   *
   * @throws Exception if unable to create cache instance.
   */
  public SventonCacheImpl() throws Exception {
    try {
      CacheManager cacheManager = CacheManager.getInstance();
      cache = cacheManager.getCache(CACHE_NAME);
    } catch (CacheException ce) {
      throw new Exception("Unable to create cache instance", ce);
    }
  }

  /**
   * {@inheritDoc]
   */
  public void put(final Object cacheKey, final Object value) {
    if (cacheKey == null) {
      throw new IllegalArgumentException("Cache key cannot be null");
    }
    Element element = new Element(cacheKey.toString(), (Serializable) value);
    cache.put(element);
  }

  /**
   * {@inheritDoc]
   */
  public Object get(final Object cacheKey) {
    if (cacheKey == null) {
      throw new IllegalArgumentException("Cache key cannot be null");
    }
    String key = cacheKey.toString();
    Element element = null;
    try {
      element = cache.get(key);
    } catch (CacheException ce) {
      // Nothing to do
    }
    return element != null ? element.getValue() : null;
  }

  /**
   * {@inheritDoc]
   */
  public long getHitCount() {
    return cache.getHitCount();
  }

  /**
   * {@inheritDoc]
   */
  public long getMissCount() {
    return cache.getMissCountNotFound();
  }

}
