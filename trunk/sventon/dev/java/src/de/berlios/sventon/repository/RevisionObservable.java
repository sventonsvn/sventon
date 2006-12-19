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

/**
 * Interface to be implemented by repository change monitor classes.
 *
 * @author jesper@user.berlios.de
 */
public interface RevisionObservable {

  /**
   * Updates the observable. For each configured repository instance, polls the repository
   * and looks for new revisions to fetch and publish to registered observers.
   */
  void updateAll();

  /**
   * Updates the observable for a single configured repository instance, polls the repository
   * and looks for new revisions to fetch and publish to registered observers.
   *
   * @param instanceName Instance name
   */
  void update(final String instanceName);

  /**
   * Checks whether the observable is updating.
   *
   * @return True if the observable is buzy updating, false if not.
   */
  boolean isUpdating();

}
