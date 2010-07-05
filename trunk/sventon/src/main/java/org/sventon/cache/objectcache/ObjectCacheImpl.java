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
package org.sventon.cache.objectcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.management.ManagementService;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.apache.commons.lang.Validate;
import org.sventon.appl.ConfigDirectory;
import org.sventon.cache.CacheException;

import javax.management.MBeanServer;
import java.io.Serializable;
import java.util.List;

/**
 * Wrapper class for cache handling.
 * Uses <a href="http://ehcache.sourceforge.net/">ehcache</a> behind the scenes.
 *
 * @author jesper@sventon.org
 */
public final class ObjectCacheImpl implements ObjectCache {

  /**
   * The cache instance.
   */
  private final Cache cache;

  /**
   * The cache manager instance.
   */
  private final CacheManager cacheManager;

  /**
   * MBeanServer for JMX management.
   */
  private MBeanServer mBeanServer;

  /**
   * Constructor.
   *
   * @param cacheName           Name of the cache.
   * @param diskStorePath       Path where to store cache files
   * @param maxElementsInMemory Max elements in memory
   * @param overflowToDisk      Overflow to disk
   * @param eternal             If true, objects never expire
   * @param timeToLiveSeconds   Object time to live in seconds
   * @param timeToIdleSeconds   Object time to idle in seconds
   * @param diskPersistent      If true, cache will be stored on disk
   * @param diskExpiryThreadIntervalSeconds
   *                            Expiry thread interval
   * @throws CacheException if unable to create cache.
   */
  public ObjectCacheImpl(final String cacheName,
                         final String diskStorePath,
                         final int maxElementsInMemory,
                         final boolean overflowToDisk,
                         final boolean eternal,
                         final int timeToLiveSeconds,
                         final int timeToIdleSeconds,
                         final boolean diskPersistent,
                         final int diskExpiryThreadIntervalSeconds) throws CacheException {
    try {
      final String cacheDiskStorePath = diskStorePath != null ? diskStorePath :
          ConfigDirectory.PROPERTY_KEY_SVENTON_DIR_SYSTEM;
      cache = new Cache(cacheName, maxElementsInMemory, MemoryStoreEvictionPolicy.LRU, overflowToDisk, null,
          eternal, timeToLiveSeconds, timeToIdleSeconds, diskPersistent, diskExpiryThreadIntervalSeconds, null);
      cacheManager = new CacheManager(createConfiguration(cacheDiskStorePath));
      cache.getCacheConfiguration().setClearOnFlush(false);
      cacheManager.addCache(cache);
    } catch (net.sf.ehcache.CacheException ce) {
      throw new CacheException("Unable to create cache instance", ce);
    }
  }

  /**
   * Initializes the cache.
   */
  public void init() {
    registerMBean();
  }

  private void registerMBean() {
    if (mBeanServer != null) {
      ManagementService.registerMBeans(cacheManager, mBeanServer, true, true, true, true);
    }
  }

  protected Configuration createConfiguration(final String diskStorePath) {
    final DiskStoreConfiguration diskStoreConfiguration = new DiskStoreConfiguration();
    diskStoreConfiguration.setPath(diskStorePath);

    final Configuration configuration = new Configuration();
    configuration.addDiskStore(diskStoreConfiguration);
    configuration.addDefaultCache(new CacheConfiguration());
    return configuration;
  }

  /**
   * @param mBeanServer MBean Server instance.
   */
  public void setMBeanServer(MBeanServer mBeanServer) {
    this.mBeanServer = mBeanServer;
  }

  /**
   * {@inheritDoc}
   */
  public void put(final Object cacheKey, final Object value) {
    Validate.notNull(cacheKey, "Cache key cannot be null");
    final Element element = new Element(cacheKey.toString(), (Serializable) value);
    cache.put(element);
  }

  /**
   * {@inheritDoc}
   */
  public Object get(final Object cacheKey) {
    Validate.notNull(cacheKey, "Cache key cannot be null");
    final String key = cacheKey.toString();
    Element element = null;
    try {
      element = cache.get(key);
    } catch (net.sf.ehcache.CacheException ce) {
      // Nothing to do
    }
    return element != null ? element.getValue() : null;
  }

  /**
   * {@inheritDoc}
   */
  public void flush() {
    cache.flush();
  }

  /**
   * {@inheritDoc}
   */
  public void shutdown() throws CacheException {
    try {
      cacheManager.shutdown();
    } catch (net.sf.ehcache.CacheException ce) {
      throw new CacheException("Unable to shutdown cache instance", ce);
    }
  }

  /**
   * Gets a list of keys for cached objects.
   *
   * @return List of keys.
   */
  public List<Object> getKeys() {
    //noinspection unchecked
    return cache.getKeys();
  }
}
