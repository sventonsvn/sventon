/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.appl;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter that make sure the user trying to modify the repository configuration is logged in properly.
 *
 * @author jesper@sventon.org
 */
public class ConfigAuthorizationFilter extends OncePerRequestFilter {

  /**
   * The application.
   */
  private Application application;

  /**
   * Constructor.
   *
   * @param application Application instance.
   */
  public ConfigAuthorizationFilter(final Application application) {
    this.application = application;
  }

  /**
   * Checks if the user is already logged in to the config pages.
   *
   * @param request Request.
   * @return True if the attribute {@code isAdminLoggedIn} is set on the HTTP session.
   */
  private boolean isAlreadyLoggedIn(final HttpServletRequest request) {
    return request.getSession(true).getAttribute("isAdminLoggedIn") != null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                  final FilterChain filterChain) throws ServletException, IOException {

    logger.info("Checking authorization");

    if (application.isConfigured()) {
      if (application.isEditableConfig()) {
        if (isAlreadyLoggedIn(request)) {
          dispatchRequest(request, response, filterChain);
        } else {
          redirectToLoginPage(request, response);
        }
      } else {
        logger.debug("Already configured - returning to list repos view");
        response.sendRedirect(request.getContextPath() + "/repos/list");
      }
    } else {
      filterChain.doFilter(request, response);
    }
  }

  private void dispatchRequest(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
    request.setAttribute("isEdit", true);
    filterChain.doFilter(request, response);
  }

  private void redirectToLoginPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
    logger.debug("Login required for editing config");
    response.sendRedirect(request.getContextPath() + "/repos/configlogin");
  }
}
