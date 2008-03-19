/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
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
import de.berlios.sventon.repository.cache.CacheException;
import de.berlios.sventon.repository.cache.entrycache.EntryCacheManager;
import de.berlios.sventon.repository.cache.logmessagecache.LogMessageCacheManager;
import de.berlios.sventon.repository.cache.objectcache.ObjectCacheManager;
import de.berlios.sventon.repository.cache.revisioncache.RevisionCacheManager;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * @author jesper@users.berlios.de
 * @author patrikfr@users.berlios.de
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
  private final Map<RepositoryName, RepositoryConfiguration> repositories = new HashMap<RepositoryName, RepositoryConfiguration>();

  /**
   * Will be <code>true</code> if all parameters are ok.
   */
  private boolean configured;

  /**
   * Application configuration directory.
   */
  private final File configurationDirectory;

  /**
   * Application configuration file name.
   */
  private final String configurationFilename;

  private EntryCacheManager entryCacheManager;
  private LogMessageCacheManager logMessageCacheManager;
  private ObjectCacheManager objectCacheManager;
  private RevisionCacheManager revisionCacheManager;

  private final List<RepositoryName> updating = Collections.synchronizedList(new ArrayList<RepositoryName>());

  /**
   * Constructor.
   *
   * @param configurationDirectory Configuration root directory. Directory will be created if it does not already exist,
   *                               not {@code null} Configuration settings will be stored in this directory.
   * @param configurationFilename  Path and file name of sventon configuration file, not {@code null}
   * @throws IOException if IO error occur
   */
  public Application(final File configurationDirectory, final String configurationFilename) throws IOException {
    Validate.notNull(configurationDirectory, "Config directory cannot be null");
    Validate.notNull(configurationFilename, "Config filename cannot be null");

    this.configurationDirectory = configurationDirectory;
    if (!this.configurationDirectory.exists() && !this.configurationDirectory.mkdirs()) {
      throw new RuntimeException("Unable to create temporary directory: " + this.configurationDirectory.getAbsolutePath());
    }
    this.configurationFilename = configurationFilename;
  }

  /**
   * Initializes the sventon application.
   *
   * @throws IOException    if unable to load the instance configurations.
   * @throws CacheException if unable to initalize caches.
   */
  public void init() throws IOException, CacheException {
    initSvnSupport();
    loadRepositoryConfigurations();
    initCaches();
  }

  /**
   * Initializes the caches by registering the cache enabled instances in the cache managers.
   *
   * @throws CacheException if unable to register instances in the cache managers.
   */
  public void initCaches() throws CacheException {
    logger.info("Initializing caches");
    for (final RepositoryConfiguration repositoryConfiguration : repositories.values()) {
      final RepositoryName name = repositoryConfiguration.getName();
      if (repositoryConfiguration.isCacheUsed()) {
        logger.debug("Registering caches for instance: " + name);
        entryCacheManager.register(name);
        logMessageCacheManager.register(name);
        objectCacheManager.register(name);
        revisionCacheManager.register(name);
      } else {
        logger.debug("Caches have not been enabled for instance: " + name);
      }
    }
    logger.info("Caches initialized ok");
  }

  /**
   * Loads the instance configurations from the file at path {@code configurationDirectory / configurationFilename}
   * <p/>
   * If a config file is found an configuration is successful this instance will be marked as configured. If no file is
   * found initialization will fail silently and the instance will not be marked as configured.
   * <p/>
   * It is legal to reload an already configured {@link RepositoryConfiguration} instance.
   * {@code configurationDirectory} and {@code configurationFilename} must be set before calling this method, or bad
   * things will most certainly happen...
   *
   * @throws IOException if IO error occur during file operations.
   * @see #isConfigured()
   */
  protected void loadRepositoryConfigurations() throws IOException {
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
   * Store the instance configurations on file at path {@code configurationDirectory / configurationFilename}.
   */
  public void storeRepositoryConfigurations() {
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
   * Creates and populates a List of <code>Properties</code> instances with relevant
   * configuration values extracted from given <code>ApplicationConfiguration</code>.
   *
   * @return List of populated Properties.
   */
  protected List<Properties> getConfigurationAsProperties() {
    final List<Properties> propertyList = new ArrayList<Properties>();
    for (final RepositoryConfiguration configuration : repositories.values()) {
      propertyList.add(configuration.getAsProperties());
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
    final Set<String> repositoryNames = new HashSet<String>();
    final Properties props = new Properties();
    props.load(input);

    for (final Object object : props.keySet()) {
      final String key = (String) object;
      final String repositoryName = key.substring(0, key.indexOf("."));
      repositoryNames.add(repositoryName);
    }

    for (final String repositoryName : repositoryNames) {
      logger.info("Configuring repository: " + repositoryName);
      addRepository(RepositoryConfiguration.create(repositoryName, props));
    }

    if (getRepositoryCount() > 0) {
      logger.info(getRepositoryCount() + " instance(s) configured");
      configured = true;
    } else {
      logger.warn("Configuration property file did exist but did not contain any configuration values");
    }
  }

  /**
   * Adds an instance to the application.
   *
   * @param configuration The instance configuration to add.
   */
  public void addRepository(final RepositoryConfiguration configuration) {
    repositories.put(configuration.getName(), configuration);
  }

  /**
   * Initializes the logger and the SVNKit library.
   */
  private void initSvnSupport() {
    SVNDebugLog.setDefaultLog(new SVNLog4JAdapter("sventon.svnkit"));
    logger.info("Initializing sventon version " + Version.getVersion());

    SVNRepositoryFactoryImpl.setup();
    DAVRepositoryFactory.setup();
    FSRepositoryFactory.setup();
  }

  /**
   * Gets the repository names.
   *
   * @return Collection of repository names.
   */
  public Set<RepositoryName> getRepositoryNames() {
    return repositories.keySet();
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
   * @return The file.
   */
  public File getConfigurationFile() {
    return new File(configurationDirectory, configurationFilename);
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

}
