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
package org.sventon.repository.observer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.appl.LogEntryCacheManager;
import org.sventon.cache.logentrycache.LogEntryCache;
import org.sventon.model.LogEntry;
import org.sventon.model.RepositoryName;
import org.sventon.repository.RevisionUpdate;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for updating one or more log entry cache instances.
 *
 * @author jesper@sventon.org
 */
public final class LogEntryCacheUpdater extends AbstractRevisionObserver {

  /**
   * The static logging instance.
   */
  private static final Log LOGGER = LogFactory.getLog(LogEntryCacheUpdater.class);

  /**
   * The cache manager instance.
   */
  private final LogEntryCacheManager logEntryCacheManager;

  /**
   * Constructor.
   *
   * @param logEntryCacheManager The cache manager instance.
   */
  public LogEntryCacheUpdater(final LogEntryCacheManager logEntryCacheManager) {
    LOGGER.info("Starting");
    this.logEntryCacheManager = logEntryCacheManager;
  }

  /**
   * Updates the log entry cache with given revision information.
   *
   * @param revisionUpdate The updated revisions.
   */
  public void update(final RevisionUpdate revisionUpdate) {
    final RepositoryName repositoryName = revisionUpdate.getRepositoryName();
    final List<SVNLogEntry> revisions = revisionUpdate.getRevisions();

    LOGGER.info("Observer got [" + revisions.size() + "] updated revision(s) for repository: " + repositoryName);

    try {
      final LogEntryCache logEntryCache = logEntryCacheManager.getCache(repositoryName);
      if (revisionUpdate.isClearCacheBeforeUpdate()) {
        LOGGER.info("Clearing cache before population");
        logEntryCache.clear();
      }
      updateInternal(logEntryCache, revisions);
    } catch (final Exception ex) {
      LOGGER.warn("Could not update cache instance [" + repositoryName + "]", ex);
    }
  }

  /**
   * Internal update method. Made protected for testing purposes only.
   *
   * @param logEntryCache Cache instance
   * @param revisions       Revisions
   */
  protected void updateInternal(final LogEntryCache logEntryCache, final List<SVNLogEntry> revisions) {
    try {
      final List<LogEntry> logEntries = new ArrayList<LogEntry>();
      for (final SVNLogEntry svnLogEntry : revisions) {
        logEntries.add(new LogEntry(svnLogEntry));
      }
      logEntryCache.add(logEntries.toArray(new LogEntry[logEntries.size()]));
    } catch (Exception ce) {
      LOGGER.error("Unable to update logEntryCache", ce);
    }
  }
}
