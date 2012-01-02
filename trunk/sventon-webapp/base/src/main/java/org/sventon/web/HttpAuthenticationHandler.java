/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web;

import org.sventon.model.Credentials;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpAuthenticationHandler.
 *
 * @author jesper@sventon.org
 */
public interface HttpAuthenticationHandler {

  /**
   * HTTP Authorization header, <code>Authorization</code>.
   */
  static final String AUTHORIZATION_HEADER = "Authorization";

  /**
   * HTTP Authentication header, <code>WWW-Authenticate</code>.
   */
  static final String AUTHENTICATE_HEADER = "WWW-Authenticate";

  /**
   * Checks if the request contains a login attempt.
   *
   * @param request Request.
   * @return True if the request contains a login attempt, false if not.
   */
  boolean isLoginAttempt(final HttpServletRequest request);

  /**
   * Gets the realm.
   *
   * @return Realm.
   */
  String getRealm();

  /**
   * Sends an authentication challenge.
   *
   * @param response Response.
   */
  void sendChallenge(final HttpServletResponse response);

  /**
   * Gets the authorization scheme.
   *
   * @return Auth scheme BASIC.
   */
  String getAuthScheme();

  /**
   * Parses the credentials from given request.
   *
   * @param request Request.
   * @return Credentials.
   */
  Credentials parseCredentials(final HttpServletRequest request);
}
