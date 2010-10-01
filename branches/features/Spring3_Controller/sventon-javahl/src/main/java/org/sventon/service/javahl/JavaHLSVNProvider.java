package org.sventon.service.javahl;

import org.sventon.SVNConnectionFactory;
import org.sventon.appl.ConfigDirectory;
import org.sventon.service.RepositoryService;
import org.sventon.service.SVNProvider;

/**
 * SVN provider based on JavaHL.
 */
public class JavaHLSVNProvider extends SVNProvider {

  @Override
  public SVNConnectionFactory getConnectionFactory(ConfigDirectory configDirectory) {
    return new JavaHLConnectionFactory(configDirectory);
  }

  @Override
  public RepositoryService getRepositoryService() {
    return new JavaHLRepositoryService();
  }

}
