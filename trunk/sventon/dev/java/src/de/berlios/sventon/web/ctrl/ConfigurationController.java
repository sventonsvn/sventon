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

import de.berlios.sventon.appl.Application;
import de.berlios.sventon.appl.InstanceConfiguration;
import de.berlios.sventon.web.command.ConfigCommand;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller handling addition of instance configurations.
 *
 * @author jesper@users.berlios.de
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
   * Constructor.
   */
  protected ConfigurationController() {
    setCommandClass(ConfigCommand.class);
    setBindOnNewForm(true);
    setSessionForm(false);
  }

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
  protected ModelAndView showForm(final HttpServletRequest request,
                                  final HttpServletResponse response, final BindException e) throws IOException {

    logger.debug("showForm() started");
    logger.info("sventon configured: " + application.isConfigured());
    if (application.isConfigured()) {
      // sventon already configured - return to browser view.
      logger.debug("Already configured - returning to browser view");
      return new ModelAndView(new RedirectView("repobrowser.svn"));
    } else if (!application.getInstanceNames().isEmpty() && request.getParameter("addnew") == null) {
      //Config url is invoked with at least one repository already added
      final Map<String, Object> model = new HashMap<String, Object>();
      model.put("addedInstances", application.getInstanceNames());
      return new ModelAndView("confirmAddConfig", model);
    } else {
      //Config URL invoked for the first time in a non-configured sventon instance
      final Map<String, Object> model = new HashMap<String, Object>();
      final ConfigCommand configCommand = new ConfigCommand();
      logger.debug("'command' set to: " + configCommand);
      model.put("command", configCommand);
      model.put("addedInstances", application.getInstanceNames());
      logger.debug("Displaying the config page");
      return new ModelAndView("config", model);
    }
  }

  /**
   * {@inheritDoc}
   */
  protected ModelAndView processFormSubmission(final HttpServletRequest request,
                                               final HttpServletResponse response, final Object command,
                                               final BindException errors) throws IOException {

    logger.debug("processFormSubmission() started");
    final Map<String, Object> model = new HashMap<String, Object>();
    logger.info("sventon configuration OK: " + application.isConfigured());
    if (application.isConfigured()) {
      // sventon already configured - return to browser view.
      logger.debug("Already configured - returning to browser view");
      return new ModelAndView(new RedirectView("repobrowser.svn"));
    }

    final ConfigCommand confCommand = (ConfigCommand) command;

    if (errors.hasErrors()) {
      //noinspection unchecked
      model.putAll(errors.getModel());
      model.put("command", confCommand);
      model.put("addedInstances", application.getInstanceNames());
      return new ModelAndView("config", model);

    } else {
      //TODO: Move this copy stuff to the command
      final InstanceConfiguration instanceConfiguration = new InstanceConfiguration(confCommand.getName());
      instanceConfiguration.setRepositoryRoot(confCommand.getRepositoryURL().trim());
      instanceConfiguration.setUid(confCommand.getUsername());
      instanceConfiguration.setPwd(confCommand.getPassword());
      instanceConfiguration.setCacheUsed(confCommand.isCacheUsed());
      instanceConfiguration.setZippedDownloadsAllowed(confCommand.isZippedDownloadsAllowed());
      instanceConfiguration.enableAccessControl(confCommand.isEnableAccessControl());
      application.addInstance(confCommand.getName(), instanceConfiguration);
      model.put("addedInstances", application.getInstanceNames());
      model.put("latestAddedInstance", confCommand.getName());
      return new ModelAndView("confirmAddConfig", model);
    }

  }

}
