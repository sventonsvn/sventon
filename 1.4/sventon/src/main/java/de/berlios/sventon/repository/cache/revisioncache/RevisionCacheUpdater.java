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
public final class RevisionCacheUpdater extends AbstractRevisionObserver {

  /**
   * The static logging instance.
   */
  private static final Log LOGGER = LogFactory.getLog(RevisionCacheUpdater.class);

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
    LOGGER.info("Starting");
    this.revisionCacheManager = revisionCacheManager;
  }

  /**
   * Updates the revision cache with given revision information.
   *
   * @param revisionUpdate The updated revisions.
   */
  public void update(final RevisionUpdate revisionUpdate) {
    final String instanceName = revisionUpdate.getInstanceName();
    final List<SVNLogEntry> revisions = revisionUpdate.getRevisions();

    LOGGER.info("Observer got [" + revisions.size() + "] updated revision(s) for instance: " + instanceName);

    try {
      final RevisionCache revisionCache = revisionCacheManager.getCache(instanceName);
      updateInternal(revisionCache, revisions);
    } catch (final CacheException ex) {
      LOGGER.warn("Could not update cache instance [" + instanceName + "]", ex);
    }
  }

  /**
   * Internal update method. Made protected for testing reasons only.
   *
   * @param revisionCache Cache instance
   * @param revisions     Revisions
   */
  protected void updateInternal(final RevisionCache revisionCache, final List<SVNLogEntry> revisions) {
    try {
      for (final SVNLogEntry svnLogEntry : revisions) {
        revisionCache.add(svnLogEntry);
        revisionCache.flush();
      }
    } catch (CacheException ce) {
      LOGGER.error("Unable to update revisionCache", ce);
    }
  }
}
