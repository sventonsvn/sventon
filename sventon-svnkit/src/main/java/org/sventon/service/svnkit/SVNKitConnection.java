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
package org.sventon.service.svnkit;

import org.sventon.SVNConnection;
import org.sventon.SventonException;
import org.sventon.model.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * SVNKitConnection.
 */
public class SVNKitConnection implements SVNConnection<SVNRepository> {

  /**
   * SVNKit delegate.
   */
  private SVNRepository delegate;

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
   * @param delegate Delegate
   * @param userName Name of the user that created the connection.
   * @param rootUrl  Repository root URL
   */
  public SVNKitConnection(final SVNRepository delegate, final String userName, final SVNURL rootUrl) {
    this.delegate = delegate;
    this.url = rootUrl;
    this.userName = userName;
  }

  public SVNRepository getDelegate() {
    return delegate;
  }

  @Override
  public void closeSession() {
    delegate.closeSession();
  }

  @Override
  public SVNURL getRepositoryRootUrl() throws SventonException {
    return url;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SVNKitConnection)) return false;
    final SVNKitConnection that = (SVNKitConnection) o;
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
