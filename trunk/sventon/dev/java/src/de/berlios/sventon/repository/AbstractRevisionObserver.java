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

import java.util.List;
import java.util.Observable;

/**
 * RevisionObserver. Abstract class to be extended by
 * observers interested in getting published repository changes.
 *
 * @author jesper@user.berlios.de
 */
public abstract class AbstractRevisionObserver implements RevisionObserver {

  /**
   * Called to update the observer.
   *
   * @param observable The observable
   * @param arg        Argument object
   */
  public void update(final Observable observable, final Object arg) {
    if (observable != null && observable instanceof RevisionObservableImpl) {
      update((List<SVNLogEntry>) arg);
    }
  }

}
