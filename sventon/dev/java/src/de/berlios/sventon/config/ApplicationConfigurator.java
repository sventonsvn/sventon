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
package de.berlios.sventon.config;

import de.berlios.sventon.logging.SVNLog4JAdapter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.util.SVNDebugLog;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Application configurator class.
 * Reads given configuration file and initializes sventon.
 * <p/>
 * The class also performs JavaSVN initialization, such as setting up logging
 * and repository access. It should be instanciated once (and only once), when
 * the application starts.
 *
 * @author jesper@users.berlios.de
 * @see <a href="http://tmate.org/svn">TMate JavaSVN</a>
 */
public class ApplicationConfigurator {

  /**
   * The logging instance.
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * Constructor.
   *
   * @param configurationFile Path and file name of sventon configuration file.
   * @param configuration     Application configuration instance to populate.
   * @throws IOException if IO error occur
   */
  public ApplicationConfigurator(final String configurationFile, final ApplicationConfiguration configuration)
      throws IOException {

    if (configurationFile == null || configuration == null) {
      throw new IllegalArgumentException("Parameters cannot be null");
    }

    initApplication();

    InputStream is = null;
    try {
      is = getClass().getResourceAsStream(configurationFile);
      if (is == null) {
        // No configuration property file exist. Application not configured yet.
        logger.info("No instance has been configured yet. Access sventon web application for setup");
      } else {
        initConfiguration(is, configuration);
      }
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

  private void initApplication() {
    SVNDebugLog.setLogger(new SVNLog4JAdapter("sventon.javasvn"));
    logger.info("Initializing sventon application");
    SVNRepositoryFactoryImpl.setup();
    DAVRepositoryFactory.setup();
    FSRepositoryFactory.setup();
  }

  /**
   * Initializes the application configuration.
   * Reads given input stream and populates the global application configuration
   * with all instance configuration parameters.
   *
   * @param input InputStream
   * @throws IOException if IO error occurs.
   */
  private void initConfiguration(final InputStream input, final ApplicationConfiguration configuration) throws IOException {
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
      final InstanceConfiguration instanceConfiguration = new InstanceConfiguration();
      instanceConfiguration.setInstanceName(instanceName);
      instanceConfiguration.setRepositoryRoot((String) props.get(instanceName + ".root"));
      instanceConfiguration.setConfiguredUID((String) props.get(instanceName + ".uid"));
      instanceConfiguration.setConfiguredPWD((String) props.get(instanceName + ".pwd"));
      instanceConfiguration.setCacheUsed(Boolean.parseBoolean((String) props.get(instanceName + ".useCache")));
      instanceConfiguration.setZippedDownloadsAllowed(Boolean.parseBoolean((String) props.get(instanceName + ".allowZipDownloads")));
      configuration.addInstanceConfiguration(instanceConfiguration);
    }

    if (configuration.getInstanceCount() > 0) {
      logger.info(configuration.getInstanceCount() + " instance(s) configured");
      configuration.setConfigured(true);
    } else {
      logger.warn("Configuration property file did exist but did not contain any configuration values");
    }
  }
}


