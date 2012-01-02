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

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AbstractHttpAuthenticationHandler.
 */
public abstract class AbstractHttpAuthenticationHandler implements HttpAuthenticationHandler {

  @Override
  public boolean isLoginAttempt(final HttpServletRequest request) {
    return getAuthzHeader(request).toLowerCase().startsWith(getAuthScheme().toLowerCase());
  }

  /**
   * Gets the authorization header string.
   *
   * @param request Request.
   * @return Header string (not null)
   */
  protected String getAuthzHeader(final HttpServletRequest request) {
    return StringUtils.trimToEmpty(request.getHeader(AUTHORIZATION_HEADER));
  }

  @Override
  public void sendChallenge(final HttpServletResponse response) {
    response.setHeader(AUTHENTICATE_HEADER, getAuthScheme() + " realm=\"" + getRealm() + "\"");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

}
