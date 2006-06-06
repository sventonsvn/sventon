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
import de.berlios.sventon.repository.LogMessage;

import java.util.List;

/**
 * Contains cached log messages.
 *
 * @author jesper@users.berlios.de
 */
public interface LogMessageCache {

  /**
   * Finds occurencies of given search string among the cached log messages.
   *
   * @param queryString Index query string.
   * @return List of log messages.
   * @throws CacheException if error.
   *
   */
  List<LogMessage> find(final String queryString) throws CacheException;

  /**
   * Add one log message to the cache.
   *
   * @param logMessage The log message to cache.
   * @throws CacheException if error.
   */
  void add(final LogMessage logMessage) throws CacheException;

  /**
   * Gets the size of the log message cache, i.e. the number
   * of cached messages.
   *
   * @return Cache size
   * @throws CacheException if unable to get cache size.
   */
  int getSize() throws CacheException;

}
