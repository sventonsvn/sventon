package de.berlios.sventon.ctrl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNRepositoryLocation;

/**
 * Small wrapper class to hold connection info for the repository.
 */
public class RepositoryConfiguration {

  private String url = null;
  
  private SVNRepositoryLocation location = null;

  protected final Log log = LogFactory.getLog(getClass());
  
  /**
   * @param url
   * @throws SVNException Thrown in URL-parsing fails
   */
  public RepositoryConfiguration(String url) throws SVNException {
    super();
    this.url = url;
    log.debug("Configuring SVN Repository...");
    SVNRepositoryFactoryImpl.setup();
    DAVRepositoryFactory.setup();
    log.debug("Getting SVN location");
    location = SVNRepositoryLocation.parseURL(url);
    log.debug("Configuration done.");
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
