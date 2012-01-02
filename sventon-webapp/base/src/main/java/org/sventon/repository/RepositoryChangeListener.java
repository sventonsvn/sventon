/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.repository;

/**
 * Interface to be implemented by classes interested in getting
 * notified about repository changes.
 *
 * @author jesper@sventon.org
 */
public interface RepositoryChangeListener {

  /**
   * Called when a repository update is detected.
   *
   * @param revisionUpdate The updated revisions.
   */
  void update(final RevisionUpdate revisionUpdate);

}
