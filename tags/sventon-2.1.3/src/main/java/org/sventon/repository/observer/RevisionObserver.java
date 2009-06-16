/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.repository.observer;

import org.sventon.repository.RevisionUpdate;

import java.util.Observer;

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
   * @param revisionUpdate The updated revisions.
   */
  void update(final RevisionUpdate revisionUpdate);

}
