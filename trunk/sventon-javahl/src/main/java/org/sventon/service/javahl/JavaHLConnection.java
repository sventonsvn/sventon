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
package org.sventon.service.javahl;

import org.sventon.SVNConnection;
import org.sventon.model.Credentials;
import org.sventon.model.SVNURL;
import org.tigris.subversion.javahl.SVNClient;

/**
 * JavaHLConnection.
 *
 * @author jesper@sventon.org
 */
public class JavaHLConnection implements SVNConnection {

  /**
   * JavaHL delegate.
   */
  private final SVNClient delegate;
  private SVNURL url;
  private Credentials credentials;

  /**
   * Constructor.
   *
   * @param delegate    SVNClient delegate
   * @param url         Repository root URL
   * @param credentials Credentials
   */
  public JavaHLConnection(final SVNClient delegate, final SVNURL url, final Credentials credentials) {
    this.delegate = delegate;
    this.url = url;
    this.credentials = credentials;
  }

  public SVNClient getDelegate() {
    return delegate;
  }

  @Override
  public void closeSession() {
  }

  /**
   * @return The repository URL.
   */
  public SVNURL getUrl() {
    return url;
  }

  /**
   * @return Given credentials needed to access this repository.
   */
  public Credentials getCredentials() {
    return credentials;
  }

}
