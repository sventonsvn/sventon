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
 * This class is preferably configured using Spring.
 */
public class RepositoryConfiguration {

  /** The url. */
  private String url = null;

  /** The repository location. */
  private SVNURL svnURL = null;

  /** The logging instance. */
  protected final Log logger = LogFactory.getLog(getClass());

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

  private void configureLogging() {
    SVNDebugLog.setLogger(new SventonSVNLogger());
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
}
