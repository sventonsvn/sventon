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
package org.sventon.cache.revisioncache;

import org.sventon.cache.Cache;
import org.tmatesoft.svn.core.SVNLogEntry;

/**
 * Contains cached revisions.
 *
 * @author jesper@sventon.org
 */
public interface RevisionCache extends Cache {

  /**
   * Gets a revision by number.
   *
   * @param revision Revision number of revision to get.
   * @return The revision info
   */
  SVNLogEntry get(final long revision);

  /**
   * Add one revision to the cache.
   *
   * @param logEntry The revision info to cache.
   */
  void add(final SVNLogEntry logEntry);

  /**
   * Flush cache to disk.
   */
  void flush();
}
