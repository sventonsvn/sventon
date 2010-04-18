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
package org.sventon.cache.logentrycache;

import org.sventon.cache.Cache;
import org.sventon.model.LogMessage;

import java.util.List;

/**
 * Contains cached log messages.
 *
 * @author jesper@sventon.org
 */
public interface LogEntryCache extends Cache {

  /**
   * Initializes the log entry cache.
   */
  void init();

  /**
   * Finds occurrences of given search string among the cached log messages.
   *
   * @param queryString Index query string.
   * @return List of log messages.
   */
  List<LogMessage> find(final String queryString);

  /**
   * Finds occurrences of given search string among the cached log messages.
   *
   * @param queryString Index query string.
   * @param startDir    Directory/path to start in.
   * @return List of log messages.
   */
  List<LogMessage> find(final String queryString, final String startDir);

  /**
   * Add one log message to the cache.
   *
   * @param logMessage The log message to cache.
   */
  void add(final LogMessage... logMessage);

  /**
   * Gets the size of the log entry cache, i.e. the number
   * of cached messages.
   *
   * @return Cache size
   */
  int getSize();

  /**
   * Clears the entire cache.
   */
  void clear();

  /**
   * Shuts down the cache.
   */
  abstract void shutdown();

}
