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
package org.sventon;

import org.sventon.model.SVNURL;

/**
 * SVN connection.
 */
public interface SVNConnection<T> {

  /**
   * Closes the session.
   */
  void closeSession();

  /**
   * @return the Repository Root URL
   * @throws SventonException
   */
  SVNURL getRepositoryRootUrl() throws SventonException;


  /**
   * A SVNConnection wraps a delegate object used to get a more fine grained handle to the different SVN operations.
   * Each implementation of SVNConnection use different supporting delegates.
   *
   * @return the implementation unique delegate
   */
  T getDelegate();
}
