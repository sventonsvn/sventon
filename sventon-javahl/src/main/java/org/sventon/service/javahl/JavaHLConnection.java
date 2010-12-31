/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.service.javahl;

import org.sventon.SVNConnection;
import org.sventon.model.SVNURL;
import org.tigris.subversion.javahl.SVNClientInterface;

/**
 * JavaHLConnection.
 *
 * @author jesper@sventon.org
 */
public class JavaHLConnection implements SVNConnection<SVNClientInterface> {

  /**
   * JavaHL delegate.
   */
  private final SVNClientInterface delegate;

  private SVNURL url;

  /**
   * Constructor.
   *
   * @param delegate SVNClient delegate
   * @param rootUrl  Repository root URL
   */
  public JavaHLConnection(final SVNClientInterface delegate, final SVNURL rootUrl) {
    this.delegate = delegate;
    this.url = rootUrl;
  }

  @Override
  public SVNClientInterface getDelegate() {
    return delegate;
  }

  @Override
  public void closeSession() {
    delegate.dispose();
  }

  /**
   * @return The repository URL.
   */
  @Override
  public SVNURL getRepositoryRootUrl() {
    return url;
  }

}
