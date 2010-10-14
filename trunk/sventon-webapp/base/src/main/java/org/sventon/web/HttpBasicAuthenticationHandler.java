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
package org.sventon.web;

import org.apache.commons.codec.binary.Base64;
import org.sventon.model.Credentials;

import javax.servlet.http.HttpServletRequest;

/**
 * Implementation of a Basic HTTP authentication handler.
 *
 * @author jesper@sventon.org
 */
public class HttpBasicAuthenticationHandler extends AbstractHttpAuthenticationHandler {

  /**
   * Realm string.
   */
  private String realm;

  /**
   * Constructor.
   */
  public HttpBasicAuthenticationHandler() {
  }

  /**
   * Constructor.
   *
   * @param realm Realm.
   */
  public HttpBasicAuthenticationHandler(final String realm) {
    this.realm = realm;
  }

  @Override
  public String getAuthScheme() {
    return HttpServletRequest.BASIC_AUTH;
  }

  @Override
  public Credentials parseCredentials(final HttpServletRequest request) {
    final String header = getAuthzHeader(request);
    if (!isLoginAttempt(request)) {
      throw new IllegalArgumentException("Request does not contain any credentials");
    }
    final String authorization = header.substring(6).trim();
    final String decoded = new String(Base64.decodeBase64(authorization.getBytes()));
    final String[] credentialString = decoded.split(":");
    return new Credentials(credentialString[0], credentialString[1]);
  }

  @Override
  public String getRealm() {
    return realm;
  }
}
