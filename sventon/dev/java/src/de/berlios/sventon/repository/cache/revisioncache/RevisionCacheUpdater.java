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
package de.berlios.sventon.repository.cache.revisioncache;

import de.berlios.sventon.repository.AbstractRevisionObserver;
import de.berlios.sventon.repository.cache.CacheException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.List;

/**
 * Class responsible for updating the revision cache.
 *
 * @author jesper@users.berlios.de
 */
public class RevisionCacheUpdater extends AbstractRevisionObserver {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * The cache instance.
   */
  private RevisionCache revisionCache;

  /**
   * Constructor.
   *
   * @param revisionCache The cache instance.
   */
  public RevisionCacheUpdater(final RevisionCache revisionCache) {
    logger.info("Starting");
    this.revisionCache = revisionCache;
  }

  /**
   * Updates the revision cache with given revision information.
   *
   * @param instanceName The instance name.
   * @param revisions The new revisions
   */
  public void update(final String instanceName, final List<SVNLogEntry> revisions) {
    logger.info("Observer got [" + revisions.size() + "] updated revision(s)");
    try {
      for (final SVNLogEntry svnLogEntry : revisions) {
        revisionCache.add(svnLogEntry);
      }
    } catch (CacheException ce) {
      logger.error("Unable to update revisionCache", ce);
    }
  }
}
