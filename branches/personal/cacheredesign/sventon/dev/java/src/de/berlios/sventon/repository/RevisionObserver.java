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
package de.berlios.sventon.repository;

import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.Observer;
import java.util.List;

/**
 * RevisionObserver. Interface to be implemented by
 * observers interested in getting published repository changes.
 *
 * @author jesper@user.berlios.de
 */
public interface RevisionObserver extends Observer {

  /**
   * Called to update the observer.
   *
   * @param revisions The new revisions.
   */
  void update(final List<SVNLogEntry> revisions);

}
