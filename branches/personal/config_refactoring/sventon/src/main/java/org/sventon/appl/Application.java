/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.appl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.Version;
import org.sventon.cache.CacheException;
import org.sventon.cache.CacheManager;
import org.sventon.cache.entrycache.EntryCacheManager;
import org.sventon.cache.logmessagecache.LogMessageCacheManager;
import org.sventon.cache.objectcache.ObjectCacheManager;
import org.sventon.cache.revisioncache.RevisionCacheManager;
import org.sventon.logging.SVNLog4JAdapter;
import org.sventon.model.RepositoryName;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.util.SVNDebugLog;

import java.io.*;
import java.util.*;

/**
 * Represents the sventon application.
 * <p/>
 * Initializes sventon and performs SVNKit initialization, such as setting up logging
 * and repository access. It should be instanciated once (and only once), when
 * the application starts.
 *
 * @author jesper@sventon.org
 * @author patrik@sventon.org
 * @see <a href="http://www.svnkit.com">SVNKit</a>
 */
public final class Application {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Set of added subversion repository names.
   */
  private final Map<RepositoryName, RepositoryConfiguration> repositories = Collections.synchronizedMap(
      new HashMap<RepositoryName, RepositoryConfiguration>());

  /**
   * Will be <code>true</code> if all parameters are ok.
   */
  private boolean configured;

  /**
   * Application configuration directory.
   */
  private final File configurationRootDirectory;

  /**
   * Application configuration file name.
   */
  private final String configurationFilename;

  /**
   * Toggles the possibility to edit the config after initial setup.
   */
  private boolean editableConfig;

  /**
   * Password needed to access the config pages.
   */
  private String configPassword;

  private EntryCacheManager entryCacheManager;
  private LogMessageCacheManager logMessageCacheManager;
  private ObjectCacheManager objectCacheManager;
  private RevisionCacheManager revisionCacheManager;

  private final List<RepositoryName> updating = Collections.synchronizedList(new ArrayList<RepositoryName>());

  /**
   * Constructor.
   *
   * @param configurationRootDirectory Configuration root directory. Directory will be created if it does not already exist,
   *                                   not {@code null} Configuration settings will be stored in this directory.
   * @param configurationFilename      Path and file name of sventon configuration file, not {@code null}
   * @throws IOException if IO error occur
   */
  public Application(final File configurationRootDirectory, final String configurationFilename) throws IOException {
    Validate.notNull(configurationRootDirectory, "Config directory cannot be null");
    Validate.notNull(configurationFilename, "Config filename cannot be null");

    this.configurationRootDirectory = configurationRootDirectory;
    this.configurationFilename = configurationFilename;
  }

  /**
   * Initializes the sventon application.
   *
   * @throws IOException    if unable to load the instance configurations.
   * @throws CacheException if unable to initalize caches.
   */
  public void init() throws IOException, CacheException {
    if (!this.configurationRootDirectory.exists() && !this.configurationRootDirectory.mkdirs()) {
      throw new RuntimeException("Unable to create temporary directory: " + this.configurationRootDirectory.getAbsolutePath());
    }

    initSvnSupport();
    loadRepositoryConfigurations();
    initCaches();
  }

  /**
   * Initializes the caches by registering the cache enabled repositories in the cache managers.
   *
   * @throws CacheException if unable to register instances in the cache managers.
   */
  public void initCaches() throws CacheException {
    logger.info("Initializing caches");
    for (final RepositoryConfiguration repositoryConfiguration : repositories.values()) {
      final RepositoryName repositoryName = repositoryConfiguration.getName();
      if (repositoryConfiguration.isCacheUsed()) {
        register(entryCacheManager, repositoryName);
        register(logMessageCacheManager, repositoryName);
        register(objectCacheManager, repositoryName);
        register(revisionCacheManager, repositoryName);
      } else {
        logger.debug("Caches have not been enabled for repository: " + repositoryName);
      }
    }
    logger.info("Caches initialized ok");
  }

  private void register(final CacheManager manager, final RepositoryName repositoryName) throws CacheException {
    if (!manager.isRegistered(repositoryName)) {
      logger.debug("Registering [" + repositoryName.toString() + "] in [" + manager.getClass().getName() + "]");
      manager.register(repositoryName);
    }
  }

  /**
   * Loads the repository configurations from the file at path
   * {@code configurationRootDirectory / [repository name] / configurationFilename}
   * <p/>
   * If a config file is found an configuration is successful this repository will be marked as configured.
   * If no file is found initialization will fail silently and the repository will not be marked as configured.
   * <p/>
   * It is legal to reload an already configured {@link RepositoryConfiguration} instance.
   * {@code configurationRootDirectory} and {@code configurationFilename} must be set before calling this method, or bad
   * things will most certainly happen...
   *
   * @throws IOException if IO error occur during file operations.
   * @see #isConfigured()
   */
  protected void loadRepositoryConfigurations() throws IOException {
    final File[] configDirs = configurationRootDirectory.listFiles(
        new SventonConfigDirectoryFileFilter(configurationFilename));

    if (configDirs.length == 0) {
      logger.debug("No configuration files were found below: " + configurationRootDirectory.getAbsolutePath());
      logger.info("No repository has been configured yet. Access sventon web application to start the setup");
      return;
    }

    for (final File configDir : configDirs) {
      InputStream is = null;
      try {
        final Properties properties = new Properties();
        is = new FileInputStream(new File(configDir, configurationFilename));
        properties.load(is);
        final String repositoryName = configDir.getName();
        logger.info("Configuring repository: " + repositoryName);
        final RepositoryConfiguration configuration = RepositoryConfiguration.create(repositoryName, properties);
        configuration.setPersisted();
        addRepository(configuration);
      } finally {
        IOUtils.closeQuietly(is);
      }
    }

    if (getRepositoryCount() > 0) {
      logger.info(getRepositoryCount() + " repositories configured");
      configured = true;
    } else {
      logger.warn("Configuration property file did exist but did not contain any configuration values");
    }
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
    for (final RepositoryConfiguration repositoryConfig : repositories.values()) {
      if (!repositoryConfig.isPersisted()) {
        final File configDir = new File(configurationRootDirectory, repositoryConfig.getName().toString());
        configDir.mkdirs();

        final File configFile = new File(configDir, configurationFilename);
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
  public void addRepository(final RepositoryConfiguration configuration) {
    repositories.put(configuration.getName(), configuration);
  }

  /**
   * Removes a repository from the sventon configuration.
   *
   * @param name Name of repository to remove.
   */
  public void removeRepository(final RepositoryName name) {
    repositories.remove(name);
  }

  /**
   * Initializes the logger and the SVNKit library.
   */
  private void initSvnSupport() {
    SVNDebugLog.setDefaultLog(new SVNLog4JAdapter());
    logger.info("Initializing sventon version " + Version.getVersion());

    SVNRepositoryFactoryImpl.setup();
    DAVRepositoryFactory.setup();
    FSRepositoryFactory.setup();
  }

  /**
   * Gets the repository names, sorted alphabetically.
   *
   * @return Collection of repository names.
   */
  public Set<RepositoryName> getRepositoryNames() {
    return new TreeSet(repositories.keySet());
  }

  /**
   * Gets the repository configuration for given repository.
   *
   * @param name Repository name.
   * @return Collection of repository names.
   */
  public RepositoryConfiguration getRepositoryConfiguration(final RepositoryName name) {
    return repositories.get(name);
  }

  /**
   * Returns the number of configured repositories
   * .
   *
   * @return Number of repositories.
   */
  public int getRepositoryCount() {
    return repositories.size();
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
  synchronized void setUpdatingCache(final RepositoryName name, final boolean updating) {
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
   * Gets the configuration file.
   *
   * @param repositoryName Name of repository to get config file for.
   * @return The config file.
   */
  public File getConfigurationFileForRepository(final String repositoryName) {
    return new File(new File(configurationRootDirectory, repositoryName), configurationFilename);
  }

  /**
   * Sets the cache manager.
   *
   * @param entryCacheManager Cache manager.
   */
  public void setEntryCacheManager(final EntryCacheManager entryCacheManager) {
    this.entryCacheManager = entryCacheManager;
  }

  /**
   * Sets the cache manager.
   *
   * @param logMessageCacheManager Cache manager.
   */
  public void setLogMessageCacheManager(final LogMessageCacheManager logMessageCacheManager) {
    this.logMessageCacheManager = logMessageCacheManager;
  }

  /**
   * Sets the cache manager.
   *
   * @param objectCacheManager Cache manager.
   */
  public void setObjectCacheManager(final ObjectCacheManager objectCacheManager) {
    this.objectCacheManager = objectCacheManager;
  }

  /**
   * Sets the cache manager.
   *
   * @param revisionCacheManager Cache manager.
   */
  public void setRevisionCacheManager(final RevisionCacheManager revisionCacheManager) {
    this.revisionCacheManager = revisionCacheManager;
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
}
