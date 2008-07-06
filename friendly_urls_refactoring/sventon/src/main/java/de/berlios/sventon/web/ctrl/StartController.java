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
package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.appl.Application;
import de.berlios.sventon.appl.RepositoryName;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The {@code StartController} is the class that handles all entry point calls to the
 * application, i.e. it handles calls to sventon where no request parameters are given.
 * <p/>
 * {@code StartController} checks how many repositories are configured and
 * redirects to appropriate page. If only one repository is configured
 * the controller will redirect to the repository browser view.
 * In any other case, the {@link ListRepositoriesController} will be called
 * so that the user can choose which repository to browse.
 * <p/>
 * If no repository has been configured the user will be redirected to
 * the config page.
 *
 * @author jesper@users.berlios.de
 */
public final class StartController extends AbstractController {

  /**
   * The application.
   */
  private Application application;

  /**
   * {@inheritDoc}
   */
  protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response)
      throws Exception {

    final ModelAndView modelAndView;
    final String contextPath = getServletContext().getContextPath();
    if (!application.isConfigured()) {
      modelAndView = new ModelAndView(new RedirectView("/repos/config", true));
    } else if (application.getRepositoryCount() > 1) {
      modelAndView = new ModelAndView(new RedirectView("/repos/list", true));
    } else if (application.getRepositoryCount() == 1) {
      final RepositoryName repositoryName = application.getRepositoryNames().iterator().next();
      modelAndView = new ModelAndView(new RedirectView("/repos/" + repositoryName + "/browse/", true));
    } else {
      throw new IllegalStateException("No instance has been configured!");
    }
    return modelAndView;
  }

  /**
   * Sets the application.
   *
   * @param application Application
   */
  public void setApplication(final Application application) {
    this.application = application;
  }

}
