package org.sventon.service.javahl;

import org.sventon.ConfigDirectoryFactory;
import org.sventon.SVNConnectionFactory;
import org.sventon.service.RepositoryService;
import org.sventon.service.SVNProvider;

/**
 * SVN provider based on JavaHL.
 */
public class JavaHLSVNProvider extends SVNProvider {

  @Override
  public SVNConnectionFactory getConnectionFactory(final ConfigDirectoryFactory configDirectoryFactory) {
    return new JavaHLConnectionFactory(configDirectoryFactory);
  }

  @Override
  public RepositoryService getRepositoryService() {
    return new JavaHLRepositoryService();
  }

}
