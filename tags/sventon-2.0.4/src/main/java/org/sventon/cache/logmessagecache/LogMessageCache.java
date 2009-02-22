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
package org.sventon.cache.logmessagecache;

import org.sventon.cache.Cache;
import org.sventon.cache.CacheException;
import org.sventon.model.LogMessage;

import java.util.List;

/**
 * Contains cached log messages.
 *
 * @author jesper@sventon.org
 */
public interface LogMessageCache extends Cache {

  /**
   * Initializes the log message cache.
   *
   * @throws CacheException if unable to start up cache.
   */
  public void init() throws CacheException;

  /**
   * Finds occurencies of given search string among the cached log messages.
   *
   * @param queryString Index query string.
   * @return List of log messages.
   * @throws CacheException if error.
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

  /**
   * Clears the entire cache.
   *
   * @throws CacheException if unable to clear cache.
   */
  void clear() throws CacheException;
}
