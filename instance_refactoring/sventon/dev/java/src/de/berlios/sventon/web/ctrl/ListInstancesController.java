/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.appl.ApplicationConfiguration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller that creates a list of all configured repository instance.
 *
 * @author jesper@users.berlios.de
 */
public class ListInstancesController extends AbstractController {

  /**
   * The application configuration. Used to get all instance names.
   */
  private ApplicationConfiguration configuration;

  protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response)
      throws Exception {

    // If application config is not ok - redirect to config.jsp
    if (!configuration.isConfigured()) {
      logger.debug("sventon not configured, redirecting to 'config.svn'");
      return new ModelAndView(new RedirectView("config.svn"));
    }
    
    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("instanceNames", configuration.getInstanceNames());
    return new ModelAndView("listinstances", model);
  }

  /**
   * Sets the application configuration.
   *
   * @param configuration Configuration.
   */
  public void setConfiguration(final ApplicationConfiguration configuration) {
    this.configuration = configuration;
  }

}
