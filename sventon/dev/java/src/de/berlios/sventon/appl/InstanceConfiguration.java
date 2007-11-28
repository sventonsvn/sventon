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
  private String configuredUID;

  /**
   * If a global user is configured for repository browsing, this property
   * should be set.
   */
  private String configuredPWD;

  /**
   * Decides whether the caching feature will be used.
   */
  private boolean useCache;

  /**
   * Decides whether <i>download as zip</i> is allowed.
   */
  private boolean zipDownloadsAllowed;

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
    instanceConfiguration.setConfiguredUID((String) properties.get(instanceName + PROPERTY_KEY_USERNAME));
    instanceConfiguration.setConfiguredPWD((String) properties.get(instanceName + PROPERTY_KEY_PASSWORD));
    instanceConfiguration.setCacheUsed(
        Boolean.parseBoolean((String) properties.get(instanceName + PROPERTY_KEY_USE_CACHE)));
    instanceConfiguration.setZippedDownloadsAllowed(
        Boolean.parseBoolean((String) properties.get(instanceName + PROPERTY_KEY_ALLOW_ZIP_DOWNLOADS)));
    instanceConfiguration.rssItemsCount = Integer.parseInt(
        properties.getProperty(instanceName + PROPERTY_KEY_RSS_ITEMS_COUNT, String.valueOf(DEFAULT_RSS_ITEMS_COUNT)));
    return instanceConfiguration;
  }

  /**
   * Sets the repository root. Root URL will never end with a slash.
   *
   * @param repositoryRoot The root url.
   */
  public void setRepositoryRoot(final String repositoryRoot) {
    repositoryURL = repositoryRoot;

    // Strip last slash if any.
    if (repositoryRoot.endsWith("/")) {
      logger.debug("Removing trailing slash from url");
      repositoryURL = repositoryRoot.substring(0, repositoryRoot.length() - 1);
    }

    try {
      svnURL = SVNURL.parseURIDecoded(repositoryURL);
    } catch (final SVNException ex) {
      logger.warn("Unable to parse URL [" + repositoryRoot + "]", ex);
    }
  }

  /**
   * Get configured Password, if any.
   *
   * @return Returns the configuredPWD.
   */
  public String getConfiguredPWD() {
    return configuredPWD;
  }

  /**
   * Set a configured password. This password will be used for repository
   * access, together with configured user ID, {@see #setConfiguredUID(String)}
   *
   * @param configuredPWD The configuredPWD to set, may be <code>null</code>.
   */
  public void setConfiguredPWD(final String configuredPWD) {
    this.configuredPWD = configuredPWD;
  }

  /**
   * Get configured user ID, if any.
   *
   * @return Returns the configuredUID.
   */
  public String getConfiguredUID() {
    return configuredUID;
  }

  /**
   * Set a configured user ID. This user ID will be used for repository access,
   * together with configured password, {@see #setConfiguredPWD(String)}
   *
   * @param configuredUID The configuredUID to set, may be <code>null</code>
   */
  public void setConfiguredUID(final String configuredUID) {
    this.configuredUID = configuredUID;
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
    return this.useCache;
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
