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
package org.sventon.cache.revisioncache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.appl.AbstractRevisionObserver;
import org.sventon.appl.RevisionUpdate;
import org.sventon.cache.CacheException;
import org.sventon.model.RepositoryName;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.List;

/**
 * Class responsible for updating one or more revision cache instances.
 *
 * @author jesper@sventon.org
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
    final RepositoryName repositoryName = revisionUpdate.getRepositoryName();
    final List<SVNLogEntry> revisions = revisionUpdate.getRevisions();

    LOGGER.info("Observer got [" + revisions.size() + "] updated revision(s) for repository: " + repositoryName);

    try {
      final RevisionCache revisionCache = revisionCacheManager.getCache(repositoryName);
      updateInternal(revisionCache, revisions);
    } catch (final CacheException ex) {
      LOGGER.warn("Could not update cache instance [" + repositoryName + "]", ex);
    }
  }

  /**
   * Internal update method. Made protected for testing reasons only.
   *
   * @param revisionCache Cache instance
   * @param revisions     Revisions
   */
  protected void updateInternal(final RevisionCache revisionCache, final List<SVNLogEntry> revisions) {
    for (final SVNLogEntry svnLogEntry : revisions) {
      revisionCache.add(svnLogEntry);
      revisionCache.flush();
    }
  }
}
