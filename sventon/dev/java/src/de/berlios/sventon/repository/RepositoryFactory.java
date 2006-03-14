/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.repository;

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


  /**
   * Singelton instance of the factory.
   */
  public static final RepositoryFactory INSTANCE = new RepositoryFactory();

  /**
   * Private constructor.
   */
  private RepositoryFactory() {
  }

  /**
   * Gets a repository instance configured using given <code>RepositoryConfiguration</code>.
   * <p/>
   * This method will assign credentials as they are set in the given <code>RepositoryConfiguration</code>.
   *
   * @param configuration The configuration
   * @return The repository instance
   * @throws SVNException if unable to create repository instance.
   */
  public SVNRepository getRepository(final RepositoryConfiguration configuration) throws SVNException {
    if (configuration == null || configuration.getSVNURL() == null) {
      return null;
    }
    SVNRepository repository = SVNRepositoryFactory.create(configuration.getSVNURL());
    if (configuration.getConfiguredUID() != null) {
      ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(new File(configuration
          .getSVNConfigurationPath()), configuration.getConfiguredUID(), configuration.getConfiguredPWD(), false);
      repository.setAuthenticationManager(authManager);
    }
    return repository;
  }


}
