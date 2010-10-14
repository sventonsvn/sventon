package org.sventon.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.ConfigDirectoryFactory;
import org.sventon.SVNConnectionFactory;

import java.util.ServiceLoader;

/**
 * Represents a Subversion provider, eg. <i>svnjavahl</i> and <i>SVNKit</i>.
 */
public abstract class SVNProvider {

  /**
   * Logger for this class and subclasses.
   */
  private static final Log LOGGER = LogFactory.getLog(SVNProvider.class);

  /**
   * @param configDirectoryFactory Factory
   * @return Connection factory
   */
  public abstract SVNConnectionFactory getConnectionFactory(final ConfigDirectoryFactory configDirectoryFactory);

  /**
   * @return Service
   */
  public abstract RepositoryService getRepositoryService();

  /**
   * Loads the first available SVN provider on the classpath.
   *
   * @return Loaded SVN provider
   */
  public static SVNProvider loadProvider() {
    final ServiceLoader<SVNProvider> loader = ServiceLoader.load(SVNProvider.class);
    for (SVNProvider svnProvider : loader) {
      LOGGER.info("Found SVNProvider: " + svnProvider.getClass());
      return svnProvider;
    }
    throw new IllegalStateException("Unable to find a SVNProvider");
  }

}
