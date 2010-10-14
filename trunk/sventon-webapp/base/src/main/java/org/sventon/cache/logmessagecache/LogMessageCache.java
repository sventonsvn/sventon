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
package org.sventon.cache.logmessagecache;

import org.sventon.cache.Cache;
import org.sventon.model.LogMessageSearchItem;

import java.util.List;

/**
 * Contains cached log entries.
 *
 * @author jesper@sventon.org
 */
public interface LogMessageCache extends Cache {

  /**
   * Initializes the log entry cache.
   */
  void init();

  /**
   * Finds occurrences of given search string among the cached log entries.
   *
   * @param queryString Index query string.
   * @return List of log entries.
   */
  List<LogMessageSearchItem> find(final String queryString);

  /**
   * Finds occurrences of given search string among the cached log entries.
   *
   * @param queryString Index query string.
   * @param startDir    Directory/path to start in.
   * @return List of log entries.
   */
  List<LogMessageSearchItem> find(final String queryString, final String startDir);

  /**
   * Add one log entry to the cache.
   *
   * @param logEntry The log entry to cache.
   */
  void add(final LogMessageSearchItem... logEntry);

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
