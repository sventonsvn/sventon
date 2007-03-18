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

import de.berlios.sventon.Version;
import de.berlios.sventon.logging.SVNLog4JAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.io.IOUtils;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.util.SVNDebugLog;

import java.io.*;
import java.util.*;

/**
 * Represents the sventon application.
 * <p/>
 * Reads given configuration instance and initializes sventon.
 * <p/>
 * The class also performs SVNKit initialization, such as setting up logging
 * and repository access. It should be instanciated once (and only once), when
 * the application starts.
 *
 * @author jesper@users.berlios.de
 * @see <a href="http://www.svnkit.com">SVNKit</a>
 */
public class Application {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Set of added subversion repository instances.
   */
  private Map<String, Instance> instances = new HashMap<String, Instance>();

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
   * Constructor.
   *
   * @param configurationDirectory Configuration root directory. Directory will be created if it does not already exist,
   *                               not {@code null} Configuration settings will be stored in this directory.
   * @param configurationFilename  Path and file name of sventon configuration file, not {@null}
   * @throws IOException if IO error occur
   */
  public Application(final File configurationDirectory, final String configurationFilename) throws IOException {
    if (configurationDirectory == null || configurationFilename == null) {
      throw new IllegalArgumentException("Parameters cannot be null");
    }
    this.configurationDirectory = configurationDirectory;
    if (!this.configurationDirectory.exists() && !this.configurationDirectory.mkdirs()) {
      throw new RuntimeException("Unable to create temporary directory: " + this.configurationDirectory.getAbsolutePath());
    }
    this.configurationFilename = configurationFilename;

    initSvnSupport();
    loadInstanceConfigurations();
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
  protected void loadInstanceConfigurations() throws IOException {
    InputStream is = null;
    final File configFile = new File(configurationDirectory, configurationFilename);
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
   * Store the instance configurations on file at path {@code configurationDirectory / configurationFilename}
   */
  public void storeInstanceConfigurations() {

    final File propertyFile = new File(configurationDirectory, configurationFilename);
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
      final InstanceConfiguration config = getInstance(instanceName).getConfiguration();
      properties.put(instanceName + InstanceConfiguration.PROPERTY_KEY_REPOSITORY_URL, config.getUrl());
      properties.put(instanceName + InstanceConfiguration.PROPERTY_KEY_USERNAME, config.getConfiguredUID());
      properties.put(instanceName + InstanceConfiguration.PROPERTY_KEY_PASSWORD, config.getConfiguredPWD());
      properties.put(instanceName + InstanceConfiguration.PROPERTY_KEY_USE_CACHE, config.isCacheUsed() ? "true" : "false");
      properties.put(instanceName + InstanceConfiguration.PROPERTY_KEY_ALLOW_ZIP_DOWNLOADS, config.isZippedDownloadsAllowed() ? "true" : "false");
      propertyList.add(properties);
    }
    return propertyList;
  }

  /**
   * Initializes the application configuration.
   * Reads given input stream and populates the global application configuration
   * with all instance configuration parameters.
   *
   * @param input InputStream
   * @throws IOException if IO error occurs.
   */
  private void initConfiguration(final InputStream input) throws IOException {
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
      addInstance(instanceName, InstanceConfiguration.create(instanceName, props));
    }

    if (getInstanceCount() > 0) {
      logger.info(getInstanceCount() + " instance(s) configured");
      configured = true;
    } else {
      logger.warn("Configuration property file did exist but did not contain any configuration values");
    }
  }

  /**
   * Adds an instance to the application.
   *
   * @param instanceName  The name of the instance.
   * @param configuration The instance configuration to add.
   */
  public void addInstance(final String instanceName, final InstanceConfiguration configuration) {
    instances.put(instanceName, new Instance(instanceName, configuration));
  }

  /**
   * Initializes the logger and the SVNKit library.
   */
  private void initSvnSupport() {
    SVNDebugLog.setDefaultLog(new SVNLog4JAdapter("sventon.svnkit"));
    logger.info("Initializing sventon version "
        + Version.getVersion() + " (revision " + Version.getRevision() + ")");

    SVNRepositoryFactoryImpl.setup();
    DAVRepositoryFactory.setup();
    FSRepositoryFactory.setup();
  }

  /**
   * Gets an instance by name.
   *
   * @param instanceName Name of instance.
   * @return Instance.
   */
  public Instance getInstance(final String instanceName) {
    final Instance instance = instances.get(instanceName);
    if (instance == null) {
      throw new RuntimeException("No instance configuration for name: " + instanceName);
    }
    return instance;
  }

  /**
   * Performs a shutdown on the application and all its repository instances. 
   */
  public void shutdown() {
    for (final Instance instance : instances.values()) {
      instance.shutdown();
    }
  }

  /**
   * Gets the instance.
   *
   * @return Collection of instances.
   */
  public Collection<Instance> getInstances() {
    return instances.values();
  }

  /**
   * Gets a set of the instance names.
   *
   * @return Set of names.
   */
  public Set<String> getInstanceNames() {
    return instances.keySet();
  }

  /**
   * Returns the number of configured instances.
   *
   * @return Number of instances.
   */
  public int getInstanceCount() {
    return instances.size();
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
