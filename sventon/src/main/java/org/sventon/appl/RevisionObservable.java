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
package org.sventon.appl;

import org.sventon.model.RepositoryName;

/**
 * Interface to be implemented by repository change monitor classes.
 *
 * @author jesper@user.berlios.de
 */
public interface RevisionObservable {

  /**
   * Updates the observable. For each configured repository, polls the repository
   * and looks for new revisions to fetch and publish to registered observers.
   */
  void updateAll();

  /**
   * Updates the observable for a single configured repository, polls the repository
   * and looks for new revisions to fetch and publish to registered observers.
   *
   * @param repositoryName   Repository name
   * @param flushAfterUpdate If <tt>true</tt>, caches will be flushed after update.
   */
  void update(final RepositoryName repositoryName, boolean flushAfterUpdate);

}
