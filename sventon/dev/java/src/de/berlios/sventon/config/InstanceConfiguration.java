/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
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
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;

/**
 * Sventon application configuration class holding configuration parameters
 * and repository connection information.
 * <p/>
 * The class also performs JavaSVN initialization, such as setting up logging
 * and repository access. It should be instanciated once (and only once), when
 * the application starts.
 * <p/>
 * This class is preferably configured using Spring.
 *
 * @author patrikfr@user.berlios.de
 * @see <a href="http://tmate.org/svn">TMate JavaSVN</a>
 * @see <a href="http://www.springframework">Spring framework</a>
 */
public class InstanceConfiguration {

  /**
   * The logging instance.
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * Instance name regex pattern.
   */
  public static final String INSTANCE_NAME_PATTERN = "[a-z0-9]+";

  /**
   * Name of sventon intance.
   */
  private String instanceName;

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
   * Constructor.
   */
  public InstanceConfiguration() {
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
    } catch (SVNException ex) {
      logger.warn("Unable to parse URL [" + repositoryRoot + "]");
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
   * Sets the name of this sventon instance configuration.
   *
   * @param instanceName Name
   * @throws IllegalArgumentException if instance name is null or does not match {@link #INSTANCE_NAME_PATTERN}.
   */
  public void setInstanceName(final String instanceName) {
    if (instanceName == null) {
      throw new IllegalArgumentException("Instance name cannot be null");
    }
    if (!instanceName.matches(INSTANCE_NAME_PATTERN)) {
      throw new IllegalArgumentException("Name must be in lower case a-z and/or 0-9");
    } else {
      this.instanceName = instanceName;
    }
  }

  /**
   * Gets the name of this sventon instance configuration.
   *
   * @return Name
   */
  public String getInstanceName() {
    return instanceName;
  }

}
