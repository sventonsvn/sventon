/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
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
 * Filter that make sure the user trying to modify the instance configuration is loggen in properly.
 *
 * @author jesper@sventon.org
 */
public class ConfigAuthorizationFilter extends OncePerRequestFilter {

  /**
   * The application.
   */
  private Application application;

  /**
   * Sets the application.
   *
   * @param application Application
   */
  public final void setApplication(final Application application) {
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
          request.setAttribute("isEdit", true);
          filterChain.doFilter(request, response);
        } else {
          logger.debug("Login required for editing config");
          response.sendRedirect(request.getContextPath() + "/repos/configlogin");
        }
      } else {
        logger.debug("Already configured - returning to list repos view");
        response.sendRedirect(request.getContextPath() + "/repos/list");
      }
    } else {
      filterChain.doFilter(request, response);
    }
  }
}
