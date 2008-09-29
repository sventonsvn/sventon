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
package org.sventon.web.ctrl;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;
import org.sventon.appl.Application;
import org.sventon.model.RepositoryName;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for deleting application configurations.
 *
 * @author jesper@sventon.org
 */
public final class DeleteConfigurationController extends AbstractController {

  /**
   * The application.
   */
  private Application application;

  /**
   * Sets the application.
   *
   * @param application Application
   */
  public void setApplication(final Application application) {
    this.application = application;
  }

  /**
   * {@inheritDoc}
   */
  protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response)
      throws Exception {

    final String repositoryToDelete = ServletRequestUtils.getStringParameter(request, "delete", null);

    if (StringUtils.hasText(repositoryToDelete)) {
      logger.info("Deleting repository configuration for [" + new RepositoryName(repositoryToDelete).toString() + "]");
      // TODO: Implement!
    }
    return new ModelAndView(new RedirectView("/repos/listconfigs", true));
  }

}