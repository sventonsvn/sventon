package de.berlios.sventon.svnsupport;

import de.berlios.sventon.ctrl.RepositoryConfiguration;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;

/**
 * RepositoryFactory.
 *
 * @author jesper@users.berlios.de
 */
public class RepositoryFactory {

  
  /** Singelton instance of the factory. */
  public static final RepositoryFactory INSTANCE = new RepositoryFactory();
  
  private static final boolean ASSIGN_CREDENTIALS = true;

  /**
   * Private constructor.
   */
  private RepositoryFactory() {
  }

  /**
   * Gets a repository instance configured using given <code>RepositoryConfiguration</code>.
   * <p>
   * This method will assign credentials as they are set in the given <code>RepositoryConfiguration</code>.
   *
   * @param configuration The configuration
   * @return The repository instance
   * @throws SVNException if unable to create repository instance.
   */
  public SVNRepository getRepository(final RepositoryConfiguration configuration) throws SVNException {
    return RepositoryFactory.INSTANCE.getRepository(configuration, ASSIGN_CREDENTIALS);
  }

  /**
   * Gets a repository instance configured using given <code>RepositoryConfiguration</code>.
   *
   * @param configuration     The configuration
   * @param assignCredentials If true, an <code>AuthenticationManager</code> will be assigned to the
   *                          repository with credentials from the given <code>configuration</code> instance.
   *                          If a user id is not configured instance (is <code>null</code>) in the
   *                          <code>configuration</code> , no <code>AuthenticationManager</code> will be set.
   * @return The repository instance. If no repository url is defined <code>null</code> will be returned. 
   * @throws SVNException if unable to create repository instance.
   */
  public SVNRepository getRepository(final RepositoryConfiguration configuration, final boolean assignCredentials) throws SVNException {
    if (configuration == null || configuration.getSVNURL() == null) {
      return null;
    }
    SVNRepository repository = SVNRepositoryFactory.create(configuration.getSVNURL());
    if (assignCredentials) {
      if (configuration.getConfiguredUID() != null) {
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(new File(configuration
            .getSVNConfigurationPath()), configuration.getConfiguredUID(), configuration.getConfiguredPWD(), false);
        repository.setAuthenticationManager(authManager);
      }
    }
    return repository;
  }

}
