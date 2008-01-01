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

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
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
   * <p/>
   * Note: Be sure to call <code>repository.closeSession()</code> when connection is not needed anymore.
   *
   * @param svnUrl Subversion URL
   * @param uid    User id
   * @param pwd    Password
   * @return The repository instance.
   * @throws SVNException if unable to create repository instance.
   */
  public SVNRepository getRepository(final SVNURL svnUrl, final String uid, final String pwd) throws SVNException {
    if (svnUrl == null) {
      return null;
    }
    final SVNRepository repository = SVNRepositoryFactory.create(svnUrl);
    if (uid != null) {

      final BasicAuthenticationManager authManager = new BasicAuthenticationManager(uid, pwd);
      repository.setAuthenticationManager(authManager);
    }
    return repository;
  }
}
