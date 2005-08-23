package de.berlios.sventon.ctrl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.util.SVNDebugLog;

import de.berlios.sventon.svnsupport.SventonSVNLogger;

/**
 * Small wrapper class to hold connection info for the repository.
 * <p>
 * The following should be configured for sventon to work propertly:
 * <ul>
 * <li>Subverison repository URL, e.g. <code>svn://svn.berlios.de/sventon/</code>
 * <li>Subversion configuration path, the path to the SVN configuration directory, the user running the 
 * servlet container running sventon must have read and write access to this directory.
 * <li>Configured Subversion user and password for repository browsing. If these properties are not configured and the 
 * repository requires user/pwd for browsing, the web application user will be promted for this information 
 * individually. Since this latter approach gives several security issues, configuring a specific user and password
 * for all sventon users to use is the preferred setup.  
 * </ul>
 * <p>
 * The class also performs JavaSVN initialization, such as setting up logging
 * and repository access. It should be instanciated once (and only once), when
 * the application starts.
 * <p>
 * This class is preferably configured using Spring.
 * 
 * @see <a href="http://tmate.org/svn">TMate JavaSVN</a>
 * @see <a href="http://www.springframework">Spring framework</a>
 */
public class RepositoryConfiguration {

  /** The logging instance. */
  protected final Log logger = LogFactory.getLog(getClass());

  /** The url. */
  private String url = null;

  /** The repository location. */
  private SVNURL svnURL = null;

  private String SVNConfigurationPath = null;

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
   * 
   * @param url The repository url.
   * @throws SVNException Thrown in URL-parsing fails
   */
  public RepositoryConfiguration(String url) throws SVNException {

    if (url == null) {
      throw new SVNException("No repository URL was provided");
    }

    configureLogging();

    // Strip last slash if any.
    if (url.endsWith("/")) {
      logger.debug("Removing trailing slash from url");
      url = url.substring(0, url.length() - 1);
    }

    this.url = url;
    logger.info("Configuring SVN Repository");
    SVNRepositoryFactoryImpl.setup();
    DAVRepositoryFactory.setup();
    logger.debug("SVN location: " + url);
    svnURL = SVNURL.parseURIDecoded(url);
    logger.debug("Configuration done");
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
   * Set a configured user ID. This user ID will be used for repository
   * access, together with configured password, {@see #setConfiguredPWD(String)}
   * 
   * @param configuredUID The configuredUID to set, may be <code>null</code>
   */
  public void setConfiguredUID(final String configuredUID) {
    this.configuredUID = configuredUID;
  }

  /**
   * Set SVN configuration path, this is a directory where Subversion configuration is stored. 
   * The user running the servlet container running sventon needs read and write access to this directory.
   * 
   * @return SVN configuration path.
   */
  public String getSVNConfigurationPath() {
    return SVNConfigurationPath;
  }

  /**
   * Get SVN configuration path. 
   * @see #setSVNConfigurationPath(String)
   * @param configurationPath Configuration path.
   */
  public void setSVNConfigurationPath(final String configurationPath) {
    SVNConfigurationPath = configurationPath;
  }

  /**
   * @return Returns the repository url.
   */
  public String getUrl() {
    return url;
  }

  /**
   * @return Returns the location.
   */
  public SVNURL getSVNURL() {
    return svnURL;
  }
  
  private void configureLogging() {
    SVNDebugLog.setLogger(new SventonSVNLogger());
  }

}
