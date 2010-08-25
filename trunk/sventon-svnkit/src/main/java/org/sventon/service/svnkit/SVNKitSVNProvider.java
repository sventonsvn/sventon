package org.sventon.service.svnkit;

import org.sventon.SVNConnectionFactory;
import org.sventon.appl.ConfigDirectory;
import org.sventon.service.RepositoryService;
import org.sventon.service.SVNProvider;

/**
 * SVN provider based on SVNKit.
 */
public class SVNKitSVNProvider extends SVNProvider {

  @Override
  public SVNConnectionFactory getConnectionFactory(ConfigDirectory configDirectory) {
    return new SVNKitConnectionFactory(configDirectory);
  }

  @Override
  public RepositoryService getRepositoryService() {
    return new SVNKitRepositoryService();
  }

}
