/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.repository;

import org.apache.commons.lang.Validate;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;

/**
 * Factory class for creating subversion repository connections.
 *
 * @author jesper@users.berlios.de
 */
public final class RepositoryFactory {

  /**
   * Root directory where to place the svn config files.
   */
  private final File configurationRootDirectory;

  /**
   * Constructor.
   *
   * @param configurationRootDirectory Root directory where to place the svn config files.
   *                                   If the directory does not exist, it will be created.
   */
  public RepositoryFactory(final File configurationRootDirectory) {
    Validate.notNull(configurationRootDirectory, "Configuration root dir cannot be null!");
    configurationRootDirectory.mkdirs();
    this.configurationRootDirectory = configurationRootDirectory;
  }

  /**
   * Gets a repository instance configured using given connection info.
   * <p/>
   * Note: Be sure to call <code>repository.closeSession()</code> when connection is not needed anymore.
   *
   * @param instanceName Instance name.
   * @param svnUrl       Subversion repository URL
   * @param uid          User id
   * @param pwd          Password
   * @return The repository instance.
   * @throws SVNException if unable to create repository instance.
   */
  public SVNRepository getRepository(final String instanceName, final SVNURL svnUrl, final String uid, final String pwd)
      throws SVNException {

    if (svnUrl == null) {
      return null;
    }

    final SVNRepository repository = SVNRepositoryFactory.create(svnUrl);
    final File configDirectory = new File(configurationRootDirectory, instanceName);
    repository.setAuthenticationManager(SVNWCUtil.createDefaultAuthenticationManager(configDirectory, uid, pwd, false));
    repository.setTunnelProvider(SVNWCUtil.createDefaultOptions(true));
    return repository;
  }
}
