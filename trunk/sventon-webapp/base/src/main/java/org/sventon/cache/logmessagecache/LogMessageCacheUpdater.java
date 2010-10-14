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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.cache.LogMessageCacheManager;
import org.sventon.model.LogEntry;
import org.sventon.model.LogMessageSearchItem;
import org.sventon.model.RepositoryName;
import org.sventon.repository.RepositoryChangeListener;
import org.sventon.repository.RevisionUpdate;

import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for updating one or more log entry cache instances.
 *
 * @author jesper@sventon.org
 */
public final class LogMessageCacheUpdater implements RepositoryChangeListener {

  /**
   * The static logging instance.
   */
  private static final Log LOGGER = LogFactory.getLog(LogMessageCacheUpdater.class);

  /**
   * The cache manager instance.
   */
  private final LogMessageCacheManager logMessageCacheManager;

  /**
   * Constructor.
   *
   * @param logMessageCacheManager The cache manager instance.
   */
  public LogMessageCacheUpdater(final LogMessageCacheManager logMessageCacheManager) {
    LOGGER.info("Starting");
    this.logMessageCacheManager = logMessageCacheManager;
  }

  /**
   * Updates the log entry cache with given revision information.
   *
   * @param revisionUpdate The updated revisions.
   */
  public void update(final RevisionUpdate revisionUpdate) {
    final RepositoryName repositoryName = revisionUpdate.getRepositoryName();
    final List<LogEntry> revisions = revisionUpdate.getRevisions();

    LOGGER.info("Listener got [" + revisions.size() + "] updated revision(s) for repository: " + repositoryName);

    try {
      final LogMessageCache logMessageCache = logMessageCacheManager.getCache(repositoryName);
      if (revisionUpdate.isClearCacheBeforeUpdate()) {
        LOGGER.info("Clearing cache before population");
        logMessageCache.clear();
      }
      updateInternal(logMessageCache, revisions);
    } catch (final Exception ex) {
      LOGGER.warn("Could not update cache instance [" + repositoryName + "]", ex);
    }
  }

  /**
   * Internal update method. Made protected for testing purposes only.
   *
   * @param logMessageCache Cache instance
   * @param revisions       Revisions
   */
  protected void updateInternal(final LogMessageCache logMessageCache, final List<LogEntry> revisions) {
    try {
      final List<LogMessageSearchItem> logEntries = new ArrayList<LogMessageSearchItem>();
      for (final LogEntry logEntry : revisions) {
        logEntries.add(new LogMessageSearchItem(logEntry));
      }
      logMessageCache.add(logEntries.toArray(new LogMessageSearchItem[logEntries.size()]));
    } catch (Exception ce) {
      LOGGER.error("Unable to update logMessageCache", ce);
    }
  }
}
