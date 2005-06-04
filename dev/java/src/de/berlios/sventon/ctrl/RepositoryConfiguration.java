package de.berlios.sventon.ctrl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepositoryLocation;

/**
 * Small wrapper class to hold connection info for the repository.
 * <p>
 * This class is preferably configured using Spring.
 */
public class RepositoryConfiguration {

  private String url = null;
  
  private SVNRepositoryLocation location = null;

  protected final Log logger = LogFactory.getLog(getClass());
  
  private String svnLogPath = null;
  

  /**
   * @param svnLogPath The svnLogPath to set.
   */
  public final void setSvnLogPath(String svnLogPath) {
    this.svnLogPath = svnLogPath;
  }

  /**
   * Configures and initializes the repository.
   * @param url The repository url.
   * @param svnLogPath Path to where svn log files will be stored.
   * @throws SVNException Thrown in URL-parsing fails
   */
  public RepositoryConfiguration(String url, final String svnLogPath) throws SVNException {
    super();
    
    
    if (url == null) {
      throw new SVNException("No repository URL was provided.");
    }
    
    this.svnLogPath = svnLogPath;
    
    configureLogging();

    // Strip last slash if any.
    if (url.endsWith("/")) {
      logger.debug("Removing trailing slash from url.");
      url = url.substring(0, url.length()-1);
    }

    this.url = url;
    logger.debug("Configuring SVN Repository...");
    SVNRepositoryFactoryImpl.setup();
    DAVRepositoryFactory.setup();
    logger.debug("Getting SVN location");

    location = SVNRepositoryLocation.parseURL(url);
    logger.debug("Configuration done.");
  }



  private void configureLogging() {
    //TODO: may throw security exception depending on security manager setting
    //TODO: how will this work in a "on JVM, multiple sventon applications" scenario?
    if (svnLogPath != null) {
      System.setProperty("javasvn.log.path", svnLogPath);
      logger.debug("Initiatedg svnlogger path: " + svnLogPath);
      
    }
    System.setProperty("javasvn.log.svn", "false");
    System.setProperty("javasvn.log.http", "false");
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
  public SVNRepositoryLocation getLocation() {
    return location;
  }  
  
  
}
