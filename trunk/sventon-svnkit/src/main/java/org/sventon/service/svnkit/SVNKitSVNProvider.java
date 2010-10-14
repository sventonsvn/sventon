package org.sventon.service.svnkit;

import org.sventon.ConfigDirectoryFactory;
import org.sventon.SVNConnectionFactory;
import org.sventon.service.RepositoryService;
import org.sventon.service.SVNProvider;

/**
 * SVN provider based on SVNKit.
 */
public class SVNKitSVNProvider extends SVNProvider {

  @Override
  public SVNConnectionFactory getConnectionFactory(final ConfigDirectoryFactory configDirectoryFactory) {
    return new SVNKitConnectionFactory(configDirectoryFactory);
  }

  @Override
  public RepositoryService getRepositoryService() {
    return new SVNKitRepositoryService();
  }

}
