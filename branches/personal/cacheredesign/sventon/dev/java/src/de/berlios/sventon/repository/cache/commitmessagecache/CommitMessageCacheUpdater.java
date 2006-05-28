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
package de.berlios.sventon.repository.cache.commitmessagecache;

import de.berlios.sventon.repository.AbstractRevisionObserver;
import de.berlios.sventon.repository.CommitMessage;
import de.berlios.sventon.repository.cache.CacheException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.List;

/**
 * CommitMessageCacheUpdater.
 *
 * @author jesper@user.berlios.de 
 */
public class CommitMessageCacheUpdater extends AbstractRevisionObserver {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * The cache instance.
   */
  private CommitMessageCache commitMessageCache;

  /**
   * Constructor.
   *
   * @param commitMessageCache The cache instance.
   */
  public CommitMessageCacheUpdater(final CommitMessageCache commitMessageCache) {
    logger.info("Starting");
    this.commitMessageCache = commitMessageCache;
  }

  /**
   * Updates the commit message cache with given revision information.
   *
   * @param revisions The new revisions
   */
  public void update(final List<SVNLogEntry> revisions) {
    logger.info("Observer got [" + revisions.size() + "] updated revision(s)");
    try {
      for (final SVNLogEntry svnLogEntry : revisions) {
        commitMessageCache.add(new CommitMessage(svnLogEntry.getRevision(), svnLogEntry.getMessage()));
      }
    } catch (CacheException ce) {
      logger.error("Unable to update commitMessageCache", ce);
    }
  }
}
