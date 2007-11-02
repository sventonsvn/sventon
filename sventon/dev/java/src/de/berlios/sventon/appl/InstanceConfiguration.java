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
public class InstanceConfiguration {

  /**
   * The logging instance.
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * The url.
   */
  private String repositoryURL;

  /**
   * The repository location.
   */
  private SVNURL svnURL;

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
  public static final String PROPERTY_KEY_USERNAME = ".uid";
  public static final String PROPERTY_KEY_PASSWORD = ".pwd";
  public static final String PROPERTY_KEY_USE_CACHE = ".useCache";
  public static final String PROPERTY_KEY_ALLOW_ZIP_DOWNLOADS = ".allowZipDownloads";
  public static final String PROPERTY_KEY_ENABLE_ACCESS_CONTROL = ".enableAccessControl";
  public static final String PROPERTY_KEY_RSS_ITEMS_COUNT = ".rssItemsCount";

  /**
   * Creates an instance using given name and properties.
   *
   * @param instanceName Instance name
   * @param properties   Properties
   * @return The InstanceConfiguration
   */
  public static InstanceConfiguration create(final String instanceName, final Properties properties) {
    final InstanceConfiguration instanceConfiguration = new InstanceConfiguration();
    instanceConfiguration.setRepositoryRoot((String) properties.get(instanceName + PROPERTY_KEY_REPOSITORY_URL));
    instanceConfiguration.setUid((String) properties.get(instanceName + PROPERTY_KEY_USERNAME));
    instanceConfiguration.setPwd((String) properties.get(instanceName + PROPERTY_KEY_PASSWORD));
    instanceConfiguration.setCacheUsed(
       Boolean.parseBoolean((String) properties.get(instanceName + PROPERTY_KEY_USE_CACHE)));
    instanceConfiguration.setZippedDownloadsAllowed(
       Boolean.parseBoolean((String) properties.get(instanceName + PROPERTY_KEY_ALLOW_ZIP_DOWNLOADS)));
    instanceConfiguration.enableAccessControl(
       Boolean.parseBoolean((String) properties.get(instanceName + PROPERTY_KEY_ENABLE_ACCESS_CONTROL)));
    instanceConfiguration.rssItemsCount = Integer.parseInt(
       properties.getProperty(instanceName + PROPERTY_KEY_RSS_ITEMS_COUNT, String.valueOf(DEFAULT_RSS_ITEMS_COUNT)));
    return instanceConfiguration;
  }

  /**
   * Sets the repository root. Root URL will never end with a slash.
   *
   * @param repositoryRoot The root url.
   */
  public void setRepositoryRoot(String repositoryRoot) {

    // Strip last slash if any.
    if (repositoryRoot.endsWith("/")) {
      logger.debug("Removing trailing slash from url");
      repositoryRoot = repositoryRoot.substring(0, repositoryRoot.length() - 1);
    }

    repositoryURL = repositoryRoot;

    try {
      svnURL = SVNURL.parseURIDecoded(repositoryURL);
    } catch (final SVNException ex) {
      logger.warn("Unable to parse URL [" + repositoryRoot + "]", ex);
    }
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
   * Set a configured password. This password will be used for repository
   * access, together with configured user ID, {@see #setUid(String)}
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
   * together with configured password, {@see #setPwd(String)}
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
  public String getUrl() {
    return repositoryURL;
  }

  /**
   * Get the SVNURL, this is the typed version of the URL set using method
   * {@link #setRepositoryRoot(String)}
   *
   * @return Returns the location.
   */
  public SVNURL getSVNURL() {
    return svnURL;
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
   * @param enableAccessControl {@code true] enables repository access control.
   */
  public void enableAccessControl(final boolean enableAccessControl) {
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
}
