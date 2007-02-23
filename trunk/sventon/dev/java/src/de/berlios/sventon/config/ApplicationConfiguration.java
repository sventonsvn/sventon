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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.io.File;

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
   * not {@code null} Configuration settings will be stored in this directory.
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
}
