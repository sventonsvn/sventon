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
package org.sventon.repository;

import org.sventon.model.RepositoryName;

/**
 * Repository change monitor.
 *
 * @author jesper@sventon.org
 */
public interface RepositoryChangeMonitor {

  /**
   * Updates the monitor. For each configured repository, the monitor will poll the repository
   * and look for new revisions to fetch and publish them to the registered listeners.
   */
  void updateAll();

  /**
   * Updates the monitor for a single configured repository, polls the repository
   * and looks for new revisions to fetch and publish to registered listeners.
   *
   * @param repositoryName Repository name
   */
  void update(final RepositoryName repositoryName);

}
