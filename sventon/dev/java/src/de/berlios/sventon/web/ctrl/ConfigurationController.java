/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.config.ApplicationConfiguration;
import de.berlios.sventon.config.InstanceConfiguration;
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
public class ConfigurationController extends AbstractFormController {

  /**
   * Application configuration.
   */
  private ApplicationConfiguration configuration;

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
   * Sets application configuration.
   *
   * @param configuration ApplicationConfiguration
   */
  public void setConfiguration(final ApplicationConfiguration configuration) {
    this.configuration = configuration;
  }

  protected ModelAndView showForm(final HttpServletRequest request,
                                  final HttpServletResponse response, final BindException e)
      throws IOException {

    logger.debug("showForm() started");
    logger.info("sventon configured: " + configuration.isConfigured());
    if (configuration.isConfigured()) {
      // sventon already configured - return to browser view.
      logger.debug("Already configured - returning to browser view");
      return new ModelAndView(new RedirectView("repobrowser.svn"));
    } else if (configuration.getInstanceNames().size() > 0 && request.getParameter("addnew") == null) {
      final Map<String, Object> model = new HashMap<String, Object>();
      model.put("addedInstances", configuration.getInstanceNames());
      return new ModelAndView("confirmAddConfig", model);
    } else {
      final Map<String, Object> model = new HashMap<String, Object>();
      final ConfigCommand configCommand = new ConfigCommand();
      logger.debug("'command' set to: " + configCommand);
      model.put("command", configCommand);
      model.put("addedInstances", configuration.getInstanceNames());
      logger.debug("Displaying the config page");
      return new ModelAndView("config", model);
    }
  }

  protected ModelAndView processFormSubmission(final HttpServletRequest request,
                                               final HttpServletResponse response, final Object command,
                                               final BindException errors) throws IOException {

    logger.debug("processFormSubmission() started");
    final Map<String, Object> model = new HashMap<String, Object>();
    logger.info("sventon configuration OK: " + configuration.isConfigured());
    if (configuration.isConfigured()) {
      // sventon already configured - return to browser view.
      logger.debug("Already configured - returning to browser view");
      return new ModelAndView(new RedirectView("repobrowser.svn"));
    }

    final ConfigCommand confCommand = (ConfigCommand) command;

    if (errors.hasErrors()) {
      //noinspection unchecked
      model.putAll(errors.getModel());
      model.put("command", confCommand);
      model.put("addedInstances", configuration.getInstanceNames());
      return new ModelAndView("config", model);

    } else {
      final InstanceConfiguration instanceConfiguration = new InstanceConfiguration();
      instanceConfiguration.setInstanceName(confCommand.getName());
      instanceConfiguration.setRepositoryRoot(confCommand.getRepositoryURL().trim());
      instanceConfiguration.setConfiguredUID(confCommand.getUsername());
      instanceConfiguration.setConfiguredPWD(confCommand.getPassword());
      instanceConfiguration.setCacheUsed(confCommand.isCacheUsed());
      instanceConfiguration.setZippedDownloadsAllowed(confCommand.isZippedDownloadsAllowed());
      configuration.addInstanceConfiguration(instanceConfiguration);
      model.put("addedInstances", configuration.getInstanceNames());
      model.put("latestAddedInstance", confCommand.getName());
      return new ModelAndView("confirmAddConfig", model);
    }

  }

}
