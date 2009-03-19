package org.sventon.web;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AbstractHttpAuthenticationHandler.
 */
public abstract class AbstractHttpAuthenticationHandler implements HttpAuthenticationHandler {

  /**
   * {@inheritDoc}
   */
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

  /**
   * {@inheritDoc}
   */
  public void sendChallenge(final HttpServletResponse response) {
    response.setHeader(AUTHENTICATE_HEADER, getAuthScheme() + " realm=\"" + getRealm() + "\"");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

}
