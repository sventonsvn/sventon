/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.appl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.sventon.Version;
import org.sventon.cache.CacheException;
import org.sventon.cache.CacheManager;
import org.sventon.model.RepositoryName;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents the sventon application.
 * <p/>
 * Initializes sventon. It should be instantiated once (and only once), when the application starts.
 *
 * @author jesper@sventon.org
 * @author patrik@sventon.org
 */
@ManagedResource
public class Application {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Holder of all repository configurations in use.
   */
  private final RepositoryConfigurations repositoryConfigurations = new RepositoryConfigurations();

  /**
   * Will be <code>true</code> if all parameters are ok.
   */
  private boolean configured;

  /**
   * Application configuration directory.
   */
  private final File repositoriesDirectory;

  /**
   * Application configuration file name.
   */
  private String configurationFileName;

  /**
   * Toggles the possibility to edit the config after initial setup.
   */
  private boolean editableConfig;

  /**
   * Password needed to access the config pages.
   */
  private String configPassword;

  /**
   * Toggles the possibility to reload the configuration from disk using a GET request to /config/reload GET.
   */
  private boolean configurationReloadSupported;

  private final List<CacheManager> cacheManagers = new ArrayList<CacheManager>();

  /**
   * Map of update markers.
   */
  private final ConcurrentLinkedQueue<RepositoryName> updating = new ConcurrentLinkedQueue<RepositoryName>();

  /**
   * System property, sventon.baseURL, used to set a base property for relative URL:s.
   */
  public static final String PROPERTY_KEY_SVENTON_BASE_URL = "sventon.baseURL";

  /**
   * Scheduler for scheduled jobs.
   */
  private Scheduler scheduler;

  /**
   * Constructor.
   *
   * @param configDirectory Configuration root directory. Directory will be created if it does not already exist,
   *                        not {@code null} Configuration settings will be stored in this directory.
   */
  public Application(final ConfigDirectory configDirectory) {
    Validate.notNull(configDirectory, "Config directory cannot be null");
    this.repositoriesDirectory = configDirectory.getRepositoriesDirectory();
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

  /**
   * Initializes the sventon application.
   *
   * @throws IOException    if unable to load the instance configurations.
   * @throws CacheException if unable to initialize caches.
   */
  @PostConstruct
  public void init() throws IOException, CacheException {
    logger.info("Initializing sventon version " + Version.getVersion());

    final File[] configDirectories = getConfigDirectories();
    if (configDirectories.length > 0) {
      loadRepositoryConfigurations(configDirectories, repositoryConfigurations);
      initCaches();
    } else {
      logger.debug("No configuration files were found below: " + repositoriesDirectory.getAbsolutePath());
      logger.info("No repository has been configured yet. Access sventon web application to start the setup");
    }

    final URL baseURL = getBaseURL();
    if (baseURL != null) {
      logger.info("Property [" + PROPERTY_KEY_SVENTON_BASE_URL + "] set to: " + baseURL);
    }
  }

  @ManagedOperation
  public synchronized void reinit() throws IOException, CacheException {
    logger.info("Starting Application reinitialization.");

    logger.debug("Reloading config data.");
    RepositoryConfigurations newConfigs = new RepositoryConfigurations();
    loadRepositoryConfigurations(getConfigDirectories(), newConfigs);

    logger.debug("Calculating diff.");
    RepositoryConfigurations.ConfigsDiff diff = repositoryConfigurations.diffByRepositoryName(newConfigs);

    //TODO: For extra security store and check timestamp for props file

    if (diff.added.isEmpty() && diff.removed.isEmpty()) {

      logger.info("New config same as old. Configuration unchanged.");

    } else {

      logger.info("Reloading configurations:");
      logger.info("  added: " + diff.added);
      logger.info("  removed: " + diff.removed);

      try {
        logger.debug("Pausing change monitor jobs");
        scheduler.pauseJob("repositoryChangeMonitorUpdateJobDetail", Scheduler.DEFAULT_GROUP);
      } catch (SchedulerException sx) {
        logger.warn(sx);
      }

      logger.debug("Applying config diff.");
      repositoryConfigurations.apply(diff);

      logger.debug("Shutting down unused caches.");
      for (RepositoryConfiguration repositoryConfiguration : diff.removed) {
        if (repositoryConfiguration.isCacheUsed()) {
          logger.info("Shutting down caches for: " + repositoryConfiguration.getName());
          shutdownAndDeregisterCaches(repositoryConfiguration);
        }
      }

      logger.debug("(Re)starting caches");
      initCaches(); //(re)start caches

      try {
        logger.debug("Resuming change monitor jobs, firing trigger.");
        scheduler.resumeJob("repositoryChangeMonitorUpdateJobDetail", Scheduler.DEFAULT_GROUP);
        scheduler.triggerJob("repositoryChangeMonitorUpdateJobDetail", Scheduler.DEFAULT_GROUP);
      } catch (SchedulerException sx) {
        logger.warn(sx);
      }

      logger.info("Cleaning up config directories.");
      cleanupOldConfigDirectories(getBackupConfigDirectories());

    }

    logger.info("Application reinitialization completed.");

  }

  private void shutdownAndDeregisterCaches(RepositoryConfiguration repositoryConfiguration) throws CacheException {
    for (CacheManager cacheManager : cacheManagers) {
      cacheManager.shutdown(repositoryConfiguration.getName());
      cacheManager.removeCache(repositoryConfiguration.getName());
    }
  }

  /**
   * @return The configuration root directories for each repository.
   */
  public File[] getConfigDirectories() {
    return repositoriesDirectory.listFiles(new SventonConfigDirectoryFileFilter(getConfigurationFileName()));
  }

  /**
   * @return The configuration root directories for each removed repository.
   */
  public File[] getBackupConfigDirectories() {
    return repositoriesDirectory.listFiles(new SventonConfigDirectoryFileFilter(getConfigurationFileBackupName()));
  }

  /**
   * Initializes the caches by registering the cache enabled repositories in the cache managers.
   *
   * @throws CacheException if unable to register instances in the cache managers.
   */
  public void initCaches() throws CacheException {
    logger.info("Initializing caches");
    for (final RepositoryConfiguration repositoryConfiguration : repositoryConfigurations.getAllConfigurations()) {
      final RepositoryName repositoryName = repositoryConfiguration.getName();
      if (repositoryConfiguration.isCacheUsed()) {
        registerCacheManagers(cacheManagers, repositoryName);
      } else {
        logger.debug("Caches have not been enabled for repository: " + repositoryName);
      }
    }
    logger.info("Caches initialized ok");
  }

  private void registerCacheManagers(final List<CacheManager> cacheManagers, final RepositoryName repositoryName) throws CacheException {
    for (CacheManager manager : cacheManagers) {
      if (!manager.isRegistered(repositoryName)) {
        logger.debug("Registering [" + repositoryName.toString() + "] in [" + manager.getClass().getName() + "]");
        manager.register(repositoryName);
      }
    }
  }

  /**
   * Loads the repository configurations from the file at path
   * {@code configurationRootDirectory / [repository name] / configurationFilename}
   * <p/>
   * If a config file is found and configuration is successful this repository will be marked as configured.
   * If no file is found initialization will fail silently and the repository will not be marked as configured.
   * <p/>
   * It is legal to reload an already configured {@link RepositoryConfiguration} instance.
   * {@code configurationRootDirectory} and {@code configurationFilename} must be set before calling this method, or bad
   * things will most certainly happen...
   *
   * @param configDirectories Repository configuration directories.
   * @param configurations    Configurations instance to load configs into. If the instance is not empty, already existing
   *                          repository configs with the same name will be overwritten.
   * @throws IOException if IO error occur during file operations.
   * @see #isConfigured()
   */
  private void loadRepositoryConfigurations(final File[] configDirectories, RepositoryConfigurations configurations)
      throws IOException {
    for (final File configDir : configDirectories) {
      InputStream is = null;
      try {
        final Properties properties = new Properties();
        is = new FileInputStream(new File(configDir, getConfigurationFileName()));
        properties.load(is);
        final String repositoryName = configDir.getName();
        logger.info("Loading repository config: " + repositoryName);
        final RepositoryConfiguration configuration = RepositoryConfiguration.create(repositoryName, properties);
        configuration.setPersisted();
        configurations.add(configuration);
      } finally {
        IOUtils.closeQuietly(is);
      }
    }

    if (hasConfigurations()) {
      logger.info(getRepositoryConfigurationCount() + " repository configuration(s) loaded");
      configured = true;
    } else {
      logger.warn("Configuration property file did exist but did not contain any configuration values");
    }
  }

  /**
   * Loads the repository configurations from the file at path
   * {@code configurationRootDirectory / [repository name] / configurationFilename}
   * <p/>
   * If a config file is found and configuration is successful this repository will be marked as configured.
   * If no file is found initialization will fail silently and the repository will not be marked as configured.
   * <p/>
   * It is legal to reload an already configured {@link RepositoryConfiguration} instance.
   * {@code configurationRootDirectory} and {@code configurationFilename} must be set before calling this method, or bad
   * things will most certainly happen...
   *
   * @param configDirectories Repository configuration directories.
   * @throws IOException if IO error occur during file operations.
   * @see #isConfigured()
   */
  protected void loadRepositoryConfigurations(final File[] configDirectories) throws IOException {
    loadRepositoryConfigurations(configDirectories, repositoryConfigurations);
  }

  /**
   * Store the repository configurations on file at path
   * {@code configurationRootDirectory / [repository name] / configurationFilename}.
   * <p/>
   * Note: Already stored configurations will be untouched.
   *
   * @throws IOException if IO error occur during file operations.
   */
  public void persistRepositoryConfigurations() throws IOException {
    for (final RepositoryConfiguration repositoryConfig : repositoryConfigurations.getAllConfigurations()) {
      if (!repositoryConfig.isPersisted()) {
        final File configDir = getConfigurationDirectoryForRepository(repositoryConfig.getName());
        if (!configDir.exists() && !configDir.mkdirs()) {
          throw new IOException("Unable to create directory: " + configDir.getAbsolutePath());
        }

        final File configFile = new File(configDir, getConfigurationFileName());
        logger.info("Storing configuration: " + configFile.getAbsolutePath());

        FileOutputStream fileOutputStream = null;
        try {
          fileOutputStream = new FileOutputStream(configFile);
          final Properties configProperties = repositoryConfig.getAsProperties();
          logger.debug("Storing properties: " + configProperties);
          configProperties.store(fileOutputStream, "");
          fileOutputStream.flush();
          repositoryConfig.setPersisted();
        } finally {
          IOUtils.closeQuietly(fileOutputStream);
        }
      }
    }
  }

  /**
   * Adds a repository to the application.
   *
   * @param configuration The repository configuration to add.
   */
  public void addConfiguration(final RepositoryConfiguration configuration) {
    repositoryConfigurations.add(configuration);
  }

  /**
   * Deletes a repository from the sventon configuration.
   * The config file
   *
   * @param name Name of repository to delete from the sventon configuration.
   */
  public void deleteConfiguration(final RepositoryName name) {
    if (!repositoryConfigurations.containsConfiguration(name)) {
      throw new IllegalArgumentException("Unknown repository name: " + name);
    }
    final RepositoryConfiguration configuration = repositoryConfigurations.getConfiguration(name);
    if (configuration.isPersisted()) {
      final File configFile = new File(getConfigurationDirectoryForRepository(name), getConfigurationFileName());
      final File configBackupFile = new File(getConfigurationDirectoryForRepository(name), getConfigurationFileBackupName());
      logger.info("Disabling repository configuration for [" + name.toString() + "]");
      if (configFile.renameTo(configBackupFile)) {
        logger.debug("Config file renamed to [" + configBackupFile.getAbsolutePath() + "]");
        repositoryConfigurations.remove(name);
      } else {
        logger.error("Unable to rename config file: " + configFile.getAbsolutePath());
      }
    } else {
      // Repository has not yet been stored and the user wants to delete it. Simply remove reference.
      repositoryConfigurations.remove(name);
    }
  }

  private String getConfigurationFileBackupName() {
    return getConfigurationFileName() + "_bak";
  }

  protected void cleanupOldConfigDirectories(File[] configDirsToDelete) {
    for (File configDir : configDirsToDelete) {
      try {
        logger.info("Deleting directory: " + configDir.getAbsolutePath());
        FileUtils.deleteDirectory(configDir);
      } catch (IOException ioe) {
        logger.warn("Config directory delete failed.", ioe);
      }
    }
  }

  /**
   * @param configurationFileName Path and file name of sventon configuration file, not {@code null}
   */
  public void setConfigurationFileName(String configurationFileName) {
    Validate.notNull(configurationFileName, "Config filename cannot be null");
    this.configurationFileName = configurationFileName;
  }

  /**
   * @return The configuration file name
   */
  public String getConfigurationFileName() {
    if (configurationFileName == null) {
      throw new IllegalStateException("Configuration file name has not been set!");
    }
    return configurationFileName;
  }

  /**
   * Gets the repository names, sorted alphabetically.
   *
   * @return Collection of repository names.
   */
  public Set<RepositoryName> getRepositoryNames() {
    return new TreeSet<RepositoryName>(repositoryConfigurations.getAllConfigurationNames());
  }

  /**
   * Gets the repository configuration for given repository.
   *
   * @param name Repository name.
   * @return Collection of repository names.
   */
  public RepositoryConfiguration getConfiguration(final RepositoryName name) {
    return repositoryConfigurations.getConfiguration(name);
  }

  /**
   * Returns the number of configured repositories
   * .
   *
   * @return Number of repositories.
   */
  public int getRepositoryConfigurationCount() {
    return repositoryConfigurations.count();
  }

  /**
   * @return True if at least one repository configuration has been added.
   */
  public boolean hasConfigurations() {
    return getRepositoryConfigurationCount() > 0;
  }

  /**
   * Gets configuration status of the sventon application.
   *
   * @return True if sventon is configured ok, false if not.
   */
  public boolean isConfigured() {
    return configured;
  }

  /**
   * Checks if a repository is being updated.
   *
   * @param name Repository name.
   * @return <tt>true</tt> if repository is being updated.
   */
  public synchronized boolean isUpdating(final RepositoryName name) {
    return updating.contains(name);
  }

  /**
   * Sets the password for the config pages.
   *
   * @param configPassword Password.
   */
  public void setConfigPassword(final String configPassword) {
    this.configPassword = configPassword;
  }

  /**
   * Checks if given password matches the configuration password.
   *
   * @param configPassword Password to match.
   * @return True if password matches, false if not.
   */
  public boolean isValidConfigPassword(final String configPassword) {
    return this.configPassword != null && this.configPassword.equals(configPassword);
  }

  /**
   * Sets the cache updating status.
   * <p/>
   * Note: This method is package protected by design.
   *
   * @param name     Repository name.
   * @param updating True or false.
   */
  public synchronized void setUpdatingCache(final RepositoryName name, final boolean updating) {
    if (updating) {
      this.updating.add(name);
    } else {
      this.updating.remove(name);
    }
  }

  /**
   * Sets the configuration status of the sventon application.
   *
   * @param configured True to indicate sventon has been configured.
   */
  public void setConfigured(final boolean configured) {
    this.configured = configured;
  }

  /**
   * Gets the configuration root directory for given repository name.
   *
   * @param repositoryName Name of repository to get config dir for.
   * @return The config root dir.
   */
  public File getConfigurationDirectoryForRepository(final RepositoryName repositoryName) {
    return new File(repositoriesDirectory, repositoryName.toString());
  }

  /**
   * Sets the cache managers.
   *
   * @param cacheManagers List of cache managers.
   */
  @Autowired
  public void setCacheManagers(final List<CacheManager> cacheManagers) {
    this.cacheManagers.addAll(cacheManagers);
  }

  /**
   * Enables or disables the possibility to edit the instance configuration.
   *
   * @param editableConfig True or false
   */
  public void setEditableConfig(final boolean editableConfig) {
    this.editableConfig = editableConfig;
  }

  /**
   * @return true if the instance configuration is editable, false if not.
   */
  public boolean isEditableConfig() {
    return editableConfig;
  }

  /**
   * @return {@code true} if it is possible to reload configurations from disk using a GET request to /config/reload.
   */
  public boolean isConfigurationReloadSupported() {
    return configurationReloadSupported;
  }

  /**
   * Enables or disables the possibility to reload configurations from disk using a GET request to /config/reload.
   *
   * @param configurationReloadSupported True to enable reload support.
   */
  public void setConfigurationReloadSupported(boolean configurationReloadSupported) {
    this.configurationReloadSupported = configurationReloadSupported;
  }

  /**
   * Gets the base URL to use for relative URL:s.
   *
   * @return The base URL or null if property <tt>sventon.baseURL</tt> was not set.
   */
  public URL getBaseURL() {
    String baseURL = StringUtils.trimToEmpty(System.getProperty(PROPERTY_KEY_SVENTON_BASE_URL));
    if (!baseURL.isEmpty()) {
      if (!baseURL.endsWith("/")) {
        baseURL = baseURL + "/";
      }
      try {
        return new URL(baseURL);
      } catch (MalformedURLException e) {
        logger.warn("Value of property '" + PROPERTY_KEY_SVENTON_BASE_URL + "' is not a valid URL: " + e.getMessage());
      }
    }
    return null;
  }
}
