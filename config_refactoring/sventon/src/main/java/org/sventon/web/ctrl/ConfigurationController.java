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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.springframework.web.servlet.view.RedirectView;
import org.sventon.appl.Application;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.web.command.ConfigCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller handling addition of instance configurations.
 *
 * @author jesper@sventon.org
 */
public final class ConfigurationController extends AbstractFormController {

  /**
   * The application.
   */
  private Application application;

  /**
   * Logger for this class and subclasses.
   */
  private final Log logger = LogFactory.getLog(getClass());

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
  protected ModelAndView showForm(final HttpServletRequest request, final HttpServletResponse response, final BindException errors)
      throws IOException {

    logger.info("sventon configured: " + application.isConfigured());

    if (application.isConfigured()) {
      logger.debug("Already configured - returning to list repos view");
      return new ModelAndView(new RedirectView("/repos/list", true));
    }

    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("addedRepositories", application.getRepositoryNames());

    if (!application.getRepositoryNames().isEmpty() && request.getParameter("addnew") == null) {
      //Config url is invoked with at least one repository already added
      return new ModelAndView("config/confirmAddConfig", model);
    } else {
      //Config URL invoked for the first time in a non-configured sventon instance
      final ConfigCommand configCommand = new ConfigCommand();
      logger.debug("'command' set to: " + configCommand);
      model.put("command", configCommand);
      return new ModelAndView("config/config", model);
    }
  }

  /**
   * {@inheritDoc}
   */
  protected ModelAndView processFormSubmission(final HttpServletRequest request,
                                               final HttpServletResponse response, final Object command,
                                               final BindException errors) throws IOException {

    logger.info("sventon configuration OK: " + application.isConfigured());

    if (application.isConfigured()) {
      logger.debug("Already configured - returning to list repos view");
      return new ModelAndView(new RedirectView("/repos/list", true));
    }

    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("addedRepositories", application.getRepositoryNames());

    final ConfigCommand confCommand = (ConfigCommand) command;

    if (errors.hasErrors()) {
      //noinspection unchecked
      model.putAll(errors.getModel());
      model.put("command", confCommand);
      return new ModelAndView("config/config", model);
    } else {
      logger.debug("Adding configuration from command: " + confCommand);
      final RepositoryConfiguration repositoryConfiguration = confCommand.createRepositoryConfiguration();
      application.addRepository(repositoryConfiguration);
      model.put("latestAddedRepository", confCommand.getName());
      return new ModelAndView("config/confirmAddConfig", model);
    }
  }

}
