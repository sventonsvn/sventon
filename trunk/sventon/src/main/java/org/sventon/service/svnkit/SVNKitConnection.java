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
package org.sventon.service.svnkit;

import org.sventon.SVNConnection;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * SVNKitConnection.
 */
public class SVNKitConnection extends SVNConnection {
  private SVNRepository delegate;

  /**
   * Constructor.
   *
   * @param delegate Delegate
   */
  public SVNKitConnection(final SVNRepository delegate) {
    this.delegate = delegate;
  }

  @Override
  public SVNRepository getDelegate() {
    return delegate;
  }

  @Override
  public void closeSession() {
    delegate.closeSession();
  }

}
