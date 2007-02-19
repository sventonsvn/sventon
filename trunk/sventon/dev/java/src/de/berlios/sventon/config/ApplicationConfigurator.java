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
package de.berlios.sventon.config;

import de.berlios.sventon.logging.SVNLog4JAdapter;
import de.berlios.sventon.Version;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.util.SVNDebugLog;

import java.io.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Application configurator class.
 * Reads given configuration file and initializes sventon.
 * <p/>
 * The class also performs SVNKit initialization, such as setting up logging
 * and repository access. It should be instanciated once (and only once), when
 * the application starts.
 *
 * @author jesper@users.berlios.de
 * @see <a href="http://www.svnkit.com">SVNKit</a>
 */
public class ApplicationConfigurator {

  /**
   * The logging instance.
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * Constructor.
   *
   * @param configuration Application configuration instance to populate.
   * @throws IOException if IO error occur
   */
  public ApplicationConfigurator(final ApplicationConfiguration configuration) throws IOException {

    if (configuration == null) {
      throw new IllegalArgumentException("Parameters cannot be null");
    }

    initApplication();

    InputStream is = null;
    final File configFile = new File(configuration.getConfigurationDirectory(), configuration.getConfigurationFilename());
    try {
      is = new FileInputStream(configFile);
      initConfiguration(is, configuration);
    } catch (FileNotFoundException fnfe) {
      logger.debug("Configuration file [" + configFile.getAbsolutePath() + "] was not found");
      logger.info("No instance has been configured yet. Access sventon web application for setup");
    } finally {
      IOUtils.closeQuietly(is);
    }
  }

  /**
   * Constructor.
   *
   * @param configurationFileStream InputStream to sventon configuration file.
   * @param configuration           Application configuration instance to populate.
   * @throws IOException if IO error occur
   */
  public ApplicationConfigurator(final InputStream configurationFileStream,
                                 final ApplicationConfiguration configuration) throws IOException {

    if (configurationFileStream == null || configuration == null) {
      throw new IllegalArgumentException("Parameters cannot be null");
    }

    initApplication();
    initConfiguration(configurationFileStream, configuration);
  }

  /**
   * Initializes the logger and the SVNKit library.
   */
  private void initApplication() {
    SVNDebugLog.setDefaultLog(new SVNLog4JAdapter("sventon.svnkit"));
    logger.info("Initializing sventon version "
        + Version.getVersion() + " (revision " + Version.getRevision() + ")");
    SVNRepositoryFactoryImpl.setup();
    DAVRepositoryFactory.setup();
    FSRepositoryFactory.setup();
  }

  /**
   * Initializes the application configuration.
   * Reads given input stream and populates the global application configuration
   * with all instance configuration parameters.
   *
   * @param input         InputStream
   * @param configuration The configuration instance
   * @throws IOException if IO error occurs.
   */
  private void initConfiguration(final InputStream input, final ApplicationConfiguration configuration)
      throws IOException {

    final Set<String> instanceNameSet = new HashSet<String>();
    final Properties props = new Properties();
    props.load(input);

    for (final Object object : props.keySet()) {
      final String key = (String) object;
      final String instanceName = key.substring(0, key.indexOf("."));
      instanceNameSet.add(instanceName);
    }

    for (final String instanceName : instanceNameSet) {
      logger.info("Configuring instance: " + instanceName);
      configuration.addInstanceConfiguration(InstanceConfiguration.create(instanceName, props));
    }

    if (configuration.getInstanceCount() > 0) {
      logger.info(configuration.getInstanceCount() + " instance(s) configured");
      configuration.setConfigured(true);
    } else {
      logger.warn("Configuration property file did exist but did not contain any configuration values");
    }
  }
}


