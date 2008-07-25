/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.repository.cache.logmessagecache;

import de.berlios.sventon.appl.AbstractRevisionObserver;
import de.berlios.sventon.appl.RevisionUpdate;
import de.berlios.sventon.repository.LogMessage;
import de.berlios.sventon.repository.cache.CacheException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.List;

/**
 * Class responsible for updating one or more log message cache instances.
 *
 * @author jesper@user.berlios.de
 */
public final class LogMessageCacheUpdater extends AbstractRevisionObserver {

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
   * Updates the log message cache with given revision information.
   *
   * @param revisionUpdate The updated revisions.
   */
  public void update(final RevisionUpdate revisionUpdate) {
    final String instanceName = revisionUpdate.getInstanceName();
    final List<SVNLogEntry> revisions = revisionUpdate.getRevisions();

    LOGGER.info("Observer got [" + revisions.size() + "] updated revision(s) for instance: " + instanceName);

    try {
      final LogMessageCache logMessageCache = logMessageCacheManager.getCache(instanceName);
      if (revisionUpdate.isClearCacheBeforeUpdate()) {
        LOGGER.info("Clearing cache before population");
        logMessageCache.clear();
      }
      updateInternal(logMessageCache, revisions);
    } catch (final Exception ex) {
      LOGGER.warn("Could not update cache instance [" + instanceName + "]", ex);
    }
  }

  /**
   * Internal update method. Made protected for testing purposes only.
   *
   * @param logMessageCache Cache instance
   * @param revisions       Revisions
   */
  protected void updateInternal(final LogMessageCache logMessageCache, final List<SVNLogEntry> revisions) {
    try {
      for (final SVNLogEntry svnLogEntry : revisions) {
        logMessageCache.add(new LogMessage(svnLogEntry.getRevision(), svnLogEntry.getMessage()));
      }
    } catch (CacheException ce) {
      LOGGER.error("Unable to update logMessageCache", ce);
    }
  }
}
