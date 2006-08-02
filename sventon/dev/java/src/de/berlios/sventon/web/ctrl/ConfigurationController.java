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

import de.berlios.sventon.web.command.ConfigCommand;
import de.berlios.sventon.config.ApplicationConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Controller handling the initial sventon configuration.
 * <p/>
 *
 * @author jesper@users.berlios.de
 */
public class ConfigurationController extends AbstractFormController {

  /**
   * Application configuration.
   */
  private ApplicationConfiguration configuration;

  /**
   * The scheduler instance. Used to fire cache update job.
   */
  private Scheduler scheduler;

  /**
   * Logger for this class and subclasses.
   */
  private final Log logger = LogFactory.getLog(getClass());

  public static final String SVENTON_PROPERTIES = "sventon.properties";
  public static final String PROPERTY_KEY_REPOSITORY_URL = "svn.root";
  public static final String PROPERTY_KEY_USERNAME = "svn.uid";
  public static final String PROPERTY_KEY_PASSWORD = "svn.pwd";
  public static final String PROPERTY_KEY_USE_CACHE = "svn.useCache";
  public static final String PROPERTY_KEY_ALLOW_ZIP_DOWNLOADS = "svn.allowZipDownloads";

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

  /**
   * Sets scheduler instance.
   * The scheduler is used to fire cache update job after configuration has been done.
   *
   * @param scheduler The scheduler
   */
  public void setScheduler(final Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  protected ModelAndView showForm(final HttpServletRequest request,
                                  final HttpServletResponse response, final BindException e)
      throws IOException {

    logger.debug("showForm() started");
    logger.info("sventon configuration ok: " + configuration.isConfigured());
    if (configuration.isConfigured()) {
      // sventon already configured - return to browser view.
      logger.debug("Already configured - returning to browser view");
      return new ModelAndView(new RedirectView("repobrowser.svn"));
    } else {
      final Map<String, Object> model = new HashMap<String, Object>();
      final ConfigCommand configCommand = new ConfigCommand();
      logger.debug("'command' set to: " + configCommand);
      model.put("command", configCommand);
      logger.debug("Displaying the config page");
      return new ModelAndView("config", model);
    }
  }

  protected ModelAndView processFormSubmission(final HttpServletRequest request,
                                               final HttpServletResponse response, final Object command,
                                               final BindException errors) throws IOException {

    logger.debug("processFormSubmission() started");
    logger.info("sventon configuration OK: " + configuration.isConfigured());
    if (configuration.isConfigured()) {
      // sventon already configured - return to browser view.
      logger.debug("Already configured - returning to browser view");
      return new ModelAndView(new RedirectView("repobrowser.svn"));
    } else {
      final ConfigCommand confCommand = (ConfigCommand) command;
      logger.debug("useCache: " + confCommand.isCacheUsed());

      if (errors.hasErrors()) {
        //noinspection unchecked
        Map<String, Object> model = errors.getModel();
        model.put("command", command);
        return new ModelAndView("config", model);
      }

      // Make sure the URL does not start or end with whitespace.
      final String trimmedURL = confCommand.getRepositoryURL().trim();
      final Properties configProperties = new Properties();
      configProperties.put(PROPERTY_KEY_REPOSITORY_URL, trimmedURL);
      configProperties.put(PROPERTY_KEY_USERNAME, confCommand.getUsername());
      configProperties.put(PROPERTY_KEY_PASSWORD, confCommand.getPassword());
      configProperties.put(PROPERTY_KEY_USE_CACHE, Boolean.TRUE == confCommand.isCacheUsed() ? "true" : "false");
      configProperties.put(PROPERTY_KEY_ALLOW_ZIP_DOWNLOADS, Boolean.TRUE == confCommand.isZippedDownloadsAllowed() ? "true" : "false");

      final String fileSeparator = System.getProperty("file.separator");

      logger.debug(configProperties.toString());

      final File propertyFile = new File(getServletContext().getRealPath("/WEB-INF/classes")
          + fileSeparator + SVENTON_PROPERTIES);
      logger.debug("Storing configuration properties in: " + propertyFile.getAbsolutePath());

      final FileOutputStream fileOutputStream = new FileOutputStream(propertyFile);
      configProperties.store(fileOutputStream, createPropertyFileComment());
      fileOutputStream.flush();
      fileOutputStream.close();

      configuration.setConfiguredUID(confCommand.getUsername());
      configuration.setConfiguredPWD(confCommand.getPassword());
      configuration.setCacheUsed(confCommand.isCacheUsed());
      configuration.setZippedDownloadsAllowed(confCommand.isZippedDownloadsAllowed());
      configuration.setRepositoryRoot(trimmedURL);

      if (configuration.isCacheUsed()) {
        try {
          logger.debug("Calling cacheService init method");
          logger.debug("Starting cache update job");
          scheduler.triggerJob("cacheUpdateJobDetail", Scheduler.DEFAULT_GROUP);
        } catch (SchedulerException sx) {
          logger.warn(sx);
        }
      }
      return new ModelAndView(new RedirectView("repobrowser.svn"));
    }
  }

  /**
   * Creates the property file comment, oldskool style.
   *
   * @return The comments.
   */
  private String createPropertyFileComment() {
    StringBuilder comments = new StringBuilder();
    comments.append("###############################################################################\n");
    comments.append("# sventon configuration file - http://sventon.berlios.de                       #\n");
    comments.append("################################################################################\n\n");
    comments.append("################################################################################\n");
    comments.append("# Key: svn.root                                                                #\n");
    comments.append("#                                                                              #\n");
    comments.append("# Description:                                                                 #\n");
    comments.append("# SVN root URL, this is the repository that will be browsable with this        #\n");
    comments.append("# sventon instance.                                                            #\n");
    comments.append("#                                                                              #\n");
    comments.append("# Example:                                                                     #\n");
    comments.append("#   svn.root=svn://svn.berlios.de/sventon/                                     #\n");
    comments.append("#   svn.root=http://domain.com/project/                                        #\n");
    comments.append("#   svn.root=svn+ssh://domain.com/project/                                     #\n");
    comments.append("################################################################################\n\n");
    comments.append("################################################################################\n");
    comments.append("# Key: svn.uid                                                                 #\n");
    comments.append("# Key: svn.pwd                                                                 #\n");
    comments.append("#                                                                              #\n");
    comments.append("# Description:                                                                 #\n");
    comments.append("# Subversion user and password to use for repository access. If not set, the   #\n");
    comments.append("# individual sventon web user will be promted for user ID and password.        #\n");
    comments.append("# The latter approach brings several security issues, assigning a dedicated    #\n");
    comments.append("# Subversion user for sventon repository browsing is the preferred approach.   #\n");
    comments.append("################################################################################\n\n");
    comments.append("################################################################################\n");
    comments.append("# Key: svn.useCache                                                            #\n");
    comments.append("#                                                                              #\n");
    comments.append("# Description:                                                                 #\n");
    comments.append("# Decides whether caching feature is enabled or not. If true, the repository   #\n");
    comments.append("# will be scanned and cached which enables the search and directory flatten    #\n");
    comments.append("# features as well as the log message search.                                  #\n");
    comments.append("################################################################################\n\n");
    comments.append("################################################################################\n");
    comments.append("# Key: svn.allowZipDownloads                                                   #\n");
    comments.append("#                                                                              #\n");
    comments.append("# Description:                                                                 #\n");
    comments.append("# Decides whether the 'download as zip' function is enabled or not.            #\n");
    comments.append("################################################################################\n\n");
    return comments.toString();
  }
}
