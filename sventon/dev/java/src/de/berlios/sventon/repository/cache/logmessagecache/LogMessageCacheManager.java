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
import de.berlios.sventon.repository.cache.CacheManager;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.File;

/**
 * Handles LogMessageCache instances.
 *
 * @author jesper@users.berlios.de
 */
public class LogMessageCacheManager extends CacheManager<LogMessageCache> {

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
   * Creates a new cache instance using given name and default settings.
   *
   * @param cacheName Name of cache instance.
   * @return The created cache instance.
   * @throws CacheException if unable to create cache.
   */
  protected LogMessageCache createCache(final String cacheName) throws CacheException {
    logger.debug("Creating cache: " + cacheName);
    final FSDirectory fsDirectory;
    try {
      final File cachePath = new File(directory, cacheName);
      cachePath.mkdirs();
      fsDirectory = FSDirectory.getDirectory(cachePath, false);
    } catch (IOException ioex) {
      throw new CacheException("Unable to create LogMessageCache instance", ioex);
    }
    return new LogMessageCacheImpl(fsDirectory);
  }

}
