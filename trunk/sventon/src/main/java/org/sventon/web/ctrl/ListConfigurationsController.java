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
package org.sventon.web.ctrl;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;
import org.sventon.appl.Application;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller listing all repository configurations, if any.
 *
 * @author jesper@sventon.org
 */
public final class ListConfigurationsController extends AbstractController {

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

  @Override
  protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response)
      throws Exception {

    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("addedRepositories", application.getRepositoryNames());

    if (hasConfigurations()) {
      return new ModelAndView("config/listConfigs", model);
    } else {
      return new ModelAndView(new RedirectView("/repos/config", true));
    }
  }

  private boolean hasConfigurations() {
    return application.getRepositoryCount() > 0;
  }

}