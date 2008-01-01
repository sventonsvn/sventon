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

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;

import java.util.Properties;

/**
 * Sventon application configuration class holding configuration parameters
 * and repository connection information for a single configured repository instance.
 *
 * @author patrikfr@user.berlios.de
 * @author jesper@users.berlios.de
 */
public final class InstanceConfiguration {

  /**
   * The logging instance.
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * Instance name.
   */
  private String instanceName;

  /**
   * The repository URL.
   */
  private String repositoryUrl;

  /**
   * The repository URL displayed to the user.
   */
  private String repositoryDisplayUrl;

  /**
   * The repository location.
   */
  private SVNURL svnUrl;

  /**
   * If a global user is configured for repository browsing, this property
   * should be set.
   */
  private String uid;

  /**
   * If a global user is configured for repository browsing, this property
   * should be set.
   */
  private String pwd;

  /**
   * Decides whether the caching feature will be used.
   */
  private boolean useCache;

  /**
   * Decides whether <i>download as zip</i> is allowed.
   */
  private boolean zipDownloadsAllowed;

  /**
   * Decides wheter repository access control is enforced (this is configured on the
   * SVN server). Note that enabling access control _disables_ caching.
   */
  private boolean enableAccessControl;

  /**
   * Number of items in the generated RSS feed.
   */
  private int rssItemsCount = DEFAULT_RSS_ITEMS_COUNT;

  /**
   * Default number of RSS feed items (20).
   */
  public static final int DEFAULT_RSS_ITEMS_COUNT = 20;
  public static final String PROPERTY_KEY_REPOSITORY_URL = ".root";
  public static final String PROPERTY_KEY_REPOSITORY_DISPLAY_URL = ".displayURL";
  public static final String PROPERTY_KEY_USERNAME = ".uid";
  public static final String PROPERTY_KEY_PASSWORD = ".pwd";
  public static final String PROPERTY_KEY_USE_CACHE = ".useCache";
  public static final String PROPERTY_KEY_ALLOW_ZIP_DOWNLOADS = ".allowZipDownloads";
  public static final String PROPERTY_KEY_ENABLE_ACCESS_CONTROL = ".enableAccessControl";
  public static final String PROPERTY_KEY_RSS_ITEMS_COUNT = ".rssItemsCount";

  /**
   * Constructor.
   *
   * @param instanceName Instance name.
   */
  public InstanceConfiguration(final String instanceName) {
    Validate.isTrue(Instance.isValidName(instanceName), "Name must be in lower case a-z and/or 0-9");
    this.instanceName = instanceName;
  }

  /**
   * Creates an instance using given name and properties.
   *
   * @param instanceName Instance name
   * @param properties   Properties
   * @return The InstanceConfiguration
   */
  public static InstanceConfiguration create(final String instanceName, final Properties properties) {
    final InstanceConfiguration instanceConfiguration = new InstanceConfiguration(instanceName);
    instanceConfiguration.setRepositoryUrl(properties.getProperty(instanceName + PROPERTY_KEY_REPOSITORY_URL));
    instanceConfiguration.setRepositoryDisplayUrl(properties.getProperty(instanceName + PROPERTY_KEY_REPOSITORY_DISPLAY_URL,
        instanceConfiguration.getRepositoryUrl()));
    instanceConfiguration.setUid(properties.getProperty(instanceName + PROPERTY_KEY_USERNAME));
    instanceConfiguration.setPwd(properties.getProperty(instanceName + PROPERTY_KEY_PASSWORD));
    instanceConfiguration.setCacheUsed(
        Boolean.parseBoolean(properties.getProperty(instanceName + PROPERTY_KEY_USE_CACHE)));
    instanceConfiguration.setZippedDownloadsAllowed(
        Boolean.parseBoolean(properties.getProperty(instanceName + PROPERTY_KEY_ALLOW_ZIP_DOWNLOADS)));
    instanceConfiguration.setEnableAccessControl(
        Boolean.parseBoolean(properties.getProperty(instanceName + PROPERTY_KEY_ENABLE_ACCESS_CONTROL)));
    instanceConfiguration.rssItemsCount = Integer.parseInt(
        properties.getProperty(instanceName + PROPERTY_KEY_RSS_ITEMS_COUNT, String.valueOf(DEFAULT_RSS_ITEMS_COUNT)));
    return instanceConfiguration;
  }

  /**
   * Gets the configuration as a properties instance.
   *
   * @return Populated properties instance.
   */
  public Properties getAsProperties() {
    final Properties properties = new Properties();
    properties.put(instanceName + InstanceConfiguration.PROPERTY_KEY_REPOSITORY_URL,
        getRepositoryUrl());
    properties.put(instanceName + InstanceConfiguration.PROPERTY_KEY_REPOSITORY_DISPLAY_URL,
        getRepositoryDisplayUrl());
    properties.put(instanceName + InstanceConfiguration.PROPERTY_KEY_USERNAME,
        getUid());
    properties.put(instanceName + InstanceConfiguration.PROPERTY_KEY_PASSWORD,
        getPwd());
    properties.put(instanceName + InstanceConfiguration.PROPERTY_KEY_USE_CACHE,
        isCacheUsed() ? "true" : "false");
    properties.put(instanceName + InstanceConfiguration.PROPERTY_KEY_ALLOW_ZIP_DOWNLOADS,
        isZippedDownloadsAllowed() ? "true" : "false");
    properties.put(instanceName + InstanceConfiguration.PROPERTY_KEY_ENABLE_ACCESS_CONTROL,
        isAccessControlEnabled() ? "true" : "false");
    properties.put(instanceName + InstanceConfiguration.PROPERTY_KEY_RSS_ITEMS_COUNT,
        String.valueOf(getRssItemsCount()));
    return properties;
  }

  /**
   * Get configured Password, if any.
   *
   * @return Returns the pwd.
   */
  public String getPwd() {
    return pwd;
  }

  /**
   * Set a configured pwd. This pwd will be used for repository
   * access, together with configured user ID, {@link #setUid(String)}
   *
   * @param pwd The pwd to set, may be <code>null</code>.
   */
  public void setPwd(final String pwd) {
    this.pwd = pwd;
  }

  /**
   * Get configured user ID, if any.
   *
   * @return Returns the uid.
   */
  public String getUid() {
    return uid;
  }

  /**
   * Set a configured user ID. This user ID will be used for repository access,
   * together with configured pwd, {@link #setPwd(String)}
   *
   * @param uid The uid to set, may be <code>null</code>
   */
  public void setUid(final String uid) {
    this.uid = uid;
  }

  /**
   * Get the configured repository URL.
   *
   * @return Returns the repository url.
   */
  public String getRepositoryUrl() {
    return repositoryUrl;
  }

  /**
   * Sets the repository root URL. Trailing slashes will be trimmed.
   *
   * @param repositoryUrl The root url.
   */
  public void setRepositoryUrl(String repositoryUrl) {

    repositoryUrl = repositoryUrl.trim();

    // Strip last slash if any.
    if (repositoryUrl.endsWith("/")) {
      logger.debug("Removing trailing slash from url");
      repositoryUrl = repositoryUrl.substring(0, repositoryUrl.length() - 1);
    }

    this.repositoryUrl = repositoryUrl;

    try {
      svnUrl = SVNURL.parseURIDecoded(this.repositoryUrl);
    } catch (final SVNException ex) {
      logger.warn("Unable to parse URL [" + repositoryUrl + "]", ex);
    }
  }

  /**
   * Sets the repository URL displayed to the user.
   *
   * @param repositoryDisplayUrl Display URL.
   */
  public void setRepositoryDisplayUrl(final String repositoryDisplayUrl) {
    this.repositoryDisplayUrl = repositoryDisplayUrl;
  }

  /**
   * Gets the repository URL displayed to the user.
   *
   * @return Display URL, never <tt>null</tt>. If no display URL is set, the
   *         repository URL will be returned instead.
   */
  public String getRepositoryDisplayUrl() {
    return repositoryDisplayUrl != null ? repositoryDisplayUrl : repositoryUrl;
  }

  /**
   * Get the SVNURL, this is the typed version of the URL set using method
   * {@link #setRepositoryUrl(String)}
   *
   * @return Returns the location.
   */
  public SVNURL getSVNURL() {
    return svnUrl;
  }

  /**
   * Sets the 'useCache' flag.
   *
   * @param useCache <code>true</code> if cache should be enabled, <code>false</code> if not.
   */
  public void setCacheUsed(final boolean useCache) {
    this.useCache = useCache;
  }

  /**
   * Checks if the cache should be used.
   *
   * @return <code>true</code> if cache is enabled, <code>false</code> if not.
   */
  public boolean isCacheUsed() {
    return this.useCache && !this.enableAccessControl;
  }

  /**
   * Checks if repository access control is enabled.
   *
   * @return {@code true} if access control is enabled.
   */
  public boolean isAccessControlEnabled() {
    return enableAccessControl;
  }

  /**
   * Sets the 'enableAccessControl' flag.
   * <b>Note</b> Enabling access control <i>disables</i> cache.
   *
   * @param enableAccessControl {@code true} enables repository access control.
   */
  public void setEnableAccessControl(final boolean enableAccessControl) {
    this.enableAccessControl = enableAccessControl;
  }

  /**
   * Sets the 'zipDownloadsAllowed' flag.
   *
   * @param zipDownloadsAllowed <code>true</code> if <i>download as zip</i> is allowed.
   */
  public void setZippedDownloadsAllowed(final boolean zipDownloadsAllowed) {
    this.zipDownloadsAllowed = zipDownloadsAllowed;
  }

  /**
   * Checks if <i>download as zip</i> is allowed.
   *
   * @return <code>true</code> if zipped downloads are allowed.
   */
  public boolean isZippedDownloadsAllowed() {
    return this.zipDownloadsAllowed;
  }

  /**
   * Gets the RSS feed items count.
   *
   * @return Number of rss feed items generated for this instance.
   */
  public int getRssItemsCount() {
    return rssItemsCount;
  }

  /**
   * Gets the instance name.
   *
   * @return
   */
  public String getInstanceName() {
    return instanceName;
  }
}
