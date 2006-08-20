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
package de.berlios.sventon.repository.cache.logmessagecache;

import de.berlios.sventon.repository.cache.CacheException;
import org.apache.lucene.store.FSDirectory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles LogMessageCache instances.
 *
 * @author jesper@users.berlios.de
 */
public class LogMessageCacheManager {

  /**
   * Logger for this class and subclasses
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * The cache instances.
   */
  final Map<String, LogMessageCache> caches = new HashMap<String, LogMessageCache>();

  /**
   * Directory where to store cache files.
   */
  private String directory;

  /**
   * Constructor.
   */
  public LogMessageCacheManager(final String rootDirectory) {
    logger.debug("Starting cache manager. Using [" + rootDirectory + "] as root directory");
    this.directory = rootDirectory;
  }

  /**
   * Gets a cache by name. If cache does not exist yet, it will be created
   * using the default settings.
   *
   * @param cacheName Name of cache to get
   * @return The cache instance.
   */
  public LogMessageCache getCache(final String cacheName) throws CacheException {
    logger.debug("Getting cache: " + cacheName);
    LogMessageCache cache = caches.get(cacheName);
    if (cache == null) {
      cache = addCache(cacheName, createCache(cacheName));
    }
    return cache;
  }

  /**
   * Creates a new cache instance using given name and default settings.
   *
   * @param cacheName Name of cache instance.
   * @return The created cache instance.
   * @throws CacheException if unable to create cache.
   */
  private LogMessageCache createCache(final String cacheName) throws CacheException {
    logger.debug("Creating cache: " + cacheName);
    final FSDirectory directory;
    try {
      directory = FSDirectory.getDirectory(this.directory, false);
    } catch (IOException ioex) {
      throw new CacheException("Unable to create LogMessageCache instance", ioex);
    }
    return new LogMessageCacheImpl(directory);
  }

  /**
   * For test purposes only.
   * Adds a cache instance to the manager's list.
   *
   * @param cacheName       Name of cache
   * @param logMessageCache Cache instance
   * @return The added cache instance
   */
  public LogMessageCache addCache(final String cacheName, final LogMessageCache logMessageCache) {
    return caches.put(cacheName, logMessageCache);
  }

  /**
   * Gets all cache instances.
   *
   * @return Cache instances.
   */
  public Map<String, LogMessageCache> getCaches() {
    return Collections.unmodifiableMap(caches);
  }

}
