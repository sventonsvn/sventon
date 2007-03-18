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
