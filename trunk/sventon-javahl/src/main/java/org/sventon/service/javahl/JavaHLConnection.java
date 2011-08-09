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

  /**
   * URL to repository root.
   */
  private SVNURL url;

  /**
   * Name of user that created connection.
   */
  private String userName;

  /**
   * Constructor.
   *
   * @param delegate SVNClient delegate
   * @param userName The name of the user that created the connection.
   * @param rootUrl  Repository root URL
   */
  public JavaHLConnection(final SVNClientInterface delegate, final String userName, final SVNURL rootUrl) {
    this.delegate = delegate;
    this.url = rootUrl;
    this.userName = userName;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof JavaHLConnection)) return false;
    final JavaHLConnection that = (JavaHLConnection) o;
    if (!url.equals(that.url)) return false;
    if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
    return true;
  }

  @Override
  public int hashCode() {
    int result = url.hashCode();
    result = 31 * result + (userName != null ? userName.hashCode() : 0);
    return result;
  }

}
