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
package de.berlios.sventon.appl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.*;

/**
 * Sventon application configuration class holding instance configuration parameters
 * and repository connection information for all configured repository instances.
 *
 * @author patrikfr@user.berlios.de
 * @author jesper@users.berlios.de
 */
public class ApplicationConfiguration {

  /**
   * The logging instance.
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * Will be <code>true</code> if all parameters are ok.
   */
  private boolean configured;

  /**
   * Application configuration directory.
   */
  private File configurationDirectory;

  /**
   * Application configuration file name.
   */
  private String configurationFilename;

  /**
   * Map of instance names and configuration.
   */
  private Map<String, InstanceConfiguration> instanceConfigurations = new HashMap<String, InstanceConfiguration>();

  /**
   * Configures and initializes the repository.
   *
   * @param configurationDirectory Configuration root directory. Directory will be created if it does not already exist,
   *                               not {@code null} Configuration settings will be stored in this directory.
   * @param configurationFilename  Path and file name of sventon configuration file, not {@null}
   */
  public ApplicationConfiguration(final File configurationDirectory, final String configurationFilename) {
    if (configurationDirectory == null || configurationFilename == null) {
      throw new IllegalArgumentException("Parameters cannot be null");
    }
    this.configurationDirectory = configurationDirectory;
    if (!this.configurationDirectory.exists() && !this.configurationDirectory.mkdirs()) {
      throw new RuntimeException("Unable to create temporary directory: " + this.configurationDirectory.getAbsolutePath());
    }
    this.configurationFilename = configurationFilename;
  }

  /**
   * Gets configuration status.
   *
   * @return True if repository is configured ok, false if not.
   */
  public boolean isConfigured() {
    return configured;
  }

  /**
   * Sets the configuration status.
   *
   * @param configured True to indicate configuration done/ok.
   */
  public void setConfigured(final boolean configured) {
    this.configured = configured;
  }

  /**
   * Adds an instance configuration to the global application configuration.
   *
   * @param configuration The configuration to add.
   */
  public void addInstanceConfiguration(final InstanceConfiguration configuration) {
    instanceConfigurations.put(configuration.getInstanceName(), configuration);
  }

  /**
   * Gets a instance configuration by instance name.
   *
   * @param instanceName Name of instance.
   * @return Corresponding instance configuration.
   * @throws RuntimeException if instance name does not exist.
   */
  public InstanceConfiguration getInstanceConfiguration(final String instanceName) {
    final InstanceConfiguration configuration = instanceConfigurations.get(instanceName);
    if (configuration == null) {
      throw new RuntimeException("No instance configuration for name: " + instanceName);
    }
    return configuration;
  }

  /**
   * Gets a set of the instance names.
   *
   * @return Set of names.
   */
  public Set<String> getInstanceNames() {
    return instanceConfigurations.keySet();
  }

  /**
   * Returns the number of configured instances.
   *
   * @return Number of instances.
   */
  public int getInstanceCount() {
    return instanceConfigurations.size();
  }

  /**
   * Gets the configuration directory.
   *
   * @return The directory
   */
  public File getConfigurationDirectory() {
    return configurationDirectory;
  }

  /**
   * Gets the configuration filename.
   *
   * @return The filename
   */
  public String getConfigurationFilename() {
    return configurationFilename;
  }

  /**
   * Loads the instance configurations from the file at path {@code configurationDirectory / configurationFilename}
   * <p/>
   * If a config file is found an configuration is successful this instance will be marked as configured. If no file is
   * found initialization will fail silently and the instance will not be marked as configured.
   * <p/>
   * It is legal to reload an already configured {@code ApplicationConfiguration] instance.
   * {@code configurationDirectory} and {@code configurationFilename} must be set before calling this method, or bad
   * things will muost certainly happen...
   *
   * @throws IOException if IO error occur during file operations.
   * @see #isConfigured()
   */
  public void loadInstanceConfigurations() throws IOException {
    InputStream is = null;
    final File configFile = new File(getConfigurationDirectory(), getConfigurationFilename());
    try {
      is = new FileInputStream(configFile);
      initConfiguration(is);
    } catch (FileNotFoundException fnfe) {
      logger.debug("Configuration file [" + configFile.getAbsolutePath() + "] was not found");
      logger.info("No instance has been configured yet. Access sventon web application for setup");
    } finally {
      IOUtils.closeQuietly(is);
    }

  }

  /**
   * Initializes the application configuration.
   * Reads given input stream and populates the global application configuration
   * with all instance configuration parameters.
   *
   * @param input InputStream
   * @throws IOException if IO error occurs.
   */
  private void initConfiguration(final InputStream input)
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
      addInstanceConfiguration(InstanceConfiguration.create(instanceName, props));
    }

    if (getInstanceCount() > 0) {
      logger.info(getInstanceCount() + " instance(s) configured");
      setConfigured(true);
    } else {
      logger.warn("Configuration property file did exist but did not contain any configuration values");
    }
  }

  /**
   * Store the instance configurations on file at path {@code configurationDirectory / configurationFilename}
   */
  public void storeInstanceConfigurations() {

    final File propertyFile = new File(getConfigurationDirectory(), getConfigurationFilename());
    logger.info("Storing configuration properties in: " + propertyFile.getAbsolutePath());

    FileOutputStream fileOutputStream = null;
    try {
      fileOutputStream = new FileOutputStream(propertyFile);
      for (final Properties properties : getConfigurationAsProperties()) {
        logger.debug("Storing: " + properties);
        properties.store(fileOutputStream, null); //This is to get the properites grouped by instance in a file.
        fileOutputStream.flush();
      }
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    } finally {
      IOUtils.closeQuietly(fileOutputStream);
    }
  }

  /**
   * Creates and populates a List of <code>Properties</code> instances with relevant configuration values
   * extracted from given <code>ApplicationConfiguration</code>.
   *
   * @return List of populated Properties.
   */
  protected List<Properties> getConfigurationAsProperties() {
    final List<Properties> propertyList = new ArrayList<Properties>();
    final Set<String> instanceNames = getInstanceNames();

    for (final String instanceName : instanceNames) {
      final Properties properties = new Properties();
      final InstanceConfiguration config = getInstanceConfiguration(instanceName);
      properties.put(instanceName + InstanceConfiguration.PROPERTY_KEY_REPOSITORY_URL, config.getUrl());
      properties.put(instanceName + InstanceConfiguration.PROPERTY_KEY_USERNAME, config.getConfiguredUID());
      properties.put(instanceName + InstanceConfiguration.PROPERTY_KEY_PASSWORD, config.getConfiguredPWD());
      properties.put(instanceName + InstanceConfiguration.PROPERTY_KEY_USE_CACHE, config.isCacheUsed() ? "true" : "false");
      properties.put(instanceName + InstanceConfiguration.PROPERTY_KEY_ALLOW_ZIP_DOWNLOADS, config.isZippedDownloadsAllowed() ? "true" : "false");
      propertyList.add(properties);
    }
    return propertyList;
  }
}
