/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.repository.cache.revisioncache;

import de.berlios.sventon.appl.AbstractRevisionObserver;
import de.berlios.sventon.appl.RevisionUpdate;
import de.berlios.sventon.repository.cache.CacheException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.List;

/**
 * Class responsible for updating one or more revision cache instances.
 *
 * @author jesper@users.berlios.de
 */
public class RevisionCacheUpdater extends AbstractRevisionObserver {

  /**
   * The static logging instance.
   */
  private static final Log logger = LogFactory.getLog(RevisionCacheUpdater.class);

  /**
   * The cache manager instance.
   */
  private final RevisionCacheManager revisionCacheManager;

  /**
   * Constructor.
   *
   * @param revisionCacheManager The cache manager instance.
   */
  public RevisionCacheUpdater(final RevisionCacheManager revisionCacheManager) {
    logger.info("Starting");
    this.revisionCacheManager = revisionCacheManager;
  }

  /**
   * Updates the revision cache with given revision information.
   *
   * @param revisionUpdate The updated revisions.
   */
  public void update(final RevisionUpdate revisionUpdate) {
    final List<SVNLogEntry> revisions = revisionUpdate.getRevisions();
    logger.info("Observer got [" + revisions.size() + "] updated revision(s) for instance: "
        + revisionUpdate.getInstanceName());

    try {
      final RevisionCache revisionCache = revisionCacheManager.getCache(revisionUpdate.getInstanceName());
      updateInternal(revisionCache, revisions);
    } catch (final CacheException ex) {
      logger.warn("Could not update cache instance [" + revisionUpdate.getInstanceName() + "]", ex);
      return;
    }
  }

  /**
   * Internal update method. Made protected for testing reasons only.
   *
   * @param revisionCache Cache instance
   * @param revisions     Revisions
   */
  protected static void updateInternal(final RevisionCache revisionCache, final List<SVNLogEntry> revisions) {
    try {
      for (final SVNLogEntry svnLogEntry : revisions) {
        revisionCache.add(svnLogEntry);
        revisionCache.flush();
      }
    } catch (CacheException ce) {
      logger.error("Unable to update revisionCache", ce);
    }
  }
}
