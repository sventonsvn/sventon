/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.repository;

import de.berlios.sventon.appl.InstanceConfiguration;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

/**
 * Factory class for creating subversion repository connections.
 *
 * @author jesper@users.berlios.de
 */
public final class RepositoryFactory {


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
   * Gets a repository instance configured using given <code>InstanceConfiguration</code>.
   * <p/>
   * This method will assign credentials as they are set in the given <code>InstanceConfiguration</code>.
   *
   * @param configuration The instance configuration.
   * @return The repository instance.
   * @throws SVNException if unable to create repository instance.
   */
  public SVNRepository getRepository(final InstanceConfiguration configuration) throws SVNException {
    if (configuration == null || configuration.getSVNURL() == null) {
      return null;
    }
    final SVNRepository repository = SVNRepositoryFactory.create(configuration.getSVNURL());
    if (configuration.getUid() != null) {
      final BasicAuthenticationManager authManager = new BasicAuthenticationManager(
          configuration.getUid(), configuration.getPwd());
      repository.setAuthenticationManager(authManager);
    }
    return repository;
  }

}
