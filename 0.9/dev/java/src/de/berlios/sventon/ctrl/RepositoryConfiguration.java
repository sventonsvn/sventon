/*
 * ====================================================================
 * Copyright (c) 2005 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.ctrl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.util.SVNDebugLog;

import de.berlios.sventon.svnsupport.SVNLog4JAdapter;

/**
 * Small wrapper class to hold connection info for the repository.
 * <p/>
 * The following should be configured for sventon to work propertly:
 * <ul>
 * <li>Subverison repository URL, e.g.
 * <code>svn://svn.berlios.de/sventon/</code>
 * <li>Subversion configuration path, the path to the SVN configuration
 * directory, the user running the servlet container running sventon must have
 * read and write access to this directory.
 * <li>Configured Subversion user and password for repository browsing. If
 * these properties are not configured and the repository requires user/pwd for
 * browsing, the web application will display an error message.
 * </ul>
 * All other configurations are optional.
 * <p/>
 * The class also performs JavaSVN initialization, such as setting up logging
 * and repository access. It should be instanciated once (and only once), when
 * the application starts.
 * <p/>
 * This class is preferably configured using Spring.
 *
 * @see <a href="http://tmate.org/svn">TMate JavaSVN</a>
 * @see <a href="http://www.springframework">Spring framework</a>
 */
public class RepositoryConfiguration {

  /**
   * Will be <code>true</code> if all parameters are ok.
   */
  private boolean configured = true;

  /**
   * The logging instance.
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * The url.
   */
  private String repositoryURL = null;

  /**
   * The repository location.
   */
  private SVNURL svnURL = null;

  /**
   * Path to the Subversion configuration libraries
   */
  private String SVNConfigurationPath = null;

  /**
   * Mount point, used to set the browser root in the repository
   */
  private String repositoryMountPoint = null;

  /**
   * If a global user is configured for repository browsing, this property
   * should be set.
   */
  private String configuredUID = null;

  /**
   * If a global user is configured for repository browsing, this property
   * should be set.
   */
  private String configuredPWD = null;

  /**
   * Configures and initializes the repository.
   */
  public RepositoryConfiguration() {

    configureLogging();
    logger.info("Configuring SVN Repository");
    SVNRepositoryFactoryImpl.setup();
    DAVRepositoryFactory.setup();
  }

  /**
   * Sets the repository root. Root URL will never end with a slash.
   *
   * @param repositoryRoot The root url.
   */
  public void setRepositoryRoot(final String repositoryRoot) {
    configured = false;
    if (repositoryRoot == null) {
      throw new IllegalArgumentException("Provided repository root url was null.");
    }
    // Strip last slash if any.
    this.repositoryURL = repositoryRoot;
    if (repositoryRoot.endsWith("/")) {
      logger.debug("Removing trailing slash from url");
      this.repositoryURL = repositoryRoot.substring(0,
          repositoryRoot.length() - 1);
    }

    logger.debug("SVN location: " + repositoryURL);

    try {
      svnURL = SVNURL.parseURIDecoded(repositoryURL);
      configured = true;
    } catch (SVNException ex) {
      logger.warn("Unable to parse URL '" + repositoryRoot
          + "'. This is normal for if sventon has not yet been configured. "
          + "Configuration mode will now be turned on");
    }
    logger.debug("sventon is configured: " + configured);
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
   * Get configured mount point. {@see #setRepositoryMountPoint(String)}
   *
   * @return Repository mount point.
   */
  public String getRepositoryMountPoint() {
    return repositoryMountPoint;
  }

  /**
   * Set a mountpoint to restrict repository browsing to a specific part of the
   * website. E.g. setting the mount point to <code>/trunk/doc</code> only the
   * <code>doc</code> directory and its subdirectories will be browsable.
   *
   * @param repositoryMountPoint Mounting point. Must be a absolute path from
   *                             the root of the repository. If the initial <code>/</code> is
   *                             missing it will be appended.
   */
  public void setRepositoryMountPoint(String repositoryMountPoint) {
    this.repositoryMountPoint = repositoryMountPoint;
  }

  /**
   * Set SVN configuration path, this is a directory where Subversion
   * configuration is stored. The user running the servlet container running
   * sventon needs read and write access to this directory.
   *
   * @return SVN configuration path.
   */
  public String getSVNConfigurationPath() {
    return SVNConfigurationPath;
  }

  /**
   * Get SVN configuration path.
   *
   * @param configurationPath Configuration path.
   * @see #setSVNConfigurationPath(String)
   */
  public void setSVNConfigurationPath(final String configurationPath) {
    SVNConfigurationPath = configurationPath;
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

  private void configureLogging() {
    SVNDebugLog.setLogger(new SVNLog4JAdapter("sventon.javasvn"));
  }

  /**
   * Gets configuration status.
   *
   * @return True if repository is configured ok, false if not.
   */
  public boolean isConfigured() {
    return configured;
  }
}
