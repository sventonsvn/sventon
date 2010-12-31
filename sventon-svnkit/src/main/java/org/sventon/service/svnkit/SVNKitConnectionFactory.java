/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.service.svnkit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.ConfigDirectoryFactory;
import org.sventon.SVNConnection;
import org.sventon.SVNConnectionFactory;
import org.sventon.SventonException;
import org.sventon.model.Credentials;
import org.sventon.model.RepositoryName;
import org.sventon.model.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * Factory class responsible for creating subversion repository connections.
 *
 * @author jesper@sventon.org
 */
public class SVNKitConnectionFactory implements SVNConnectionFactory {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Root directory where to place the svn config files.
   */
  private final ConfigDirectoryFactory configDirectoryFactory;

  /**
   * Constructor.
   *
   * @param configDirectoryFactory Factory
   */
  public SVNKitConnectionFactory(final ConfigDirectoryFactory configDirectoryFactory) {
    this.configDirectoryFactory = configDirectoryFactory;
  }

  /**
   * Initializes SVNKit.
   */
  @PostConstruct
  public void init() {
    logger.info("Initializing SVNKit version " + org.tmatesoft.svn.util.Version.getSVNVersion());
    SVNRepositoryFactoryImpl.setup();
    DAVRepositoryFactory.setup();
    FSRepositoryFactory.setup();
  }

  @Override
  public SVNConnection createConnection(final RepositoryName repositoryName, final SVNURL svnUrl,
                                        final Credentials credentials) throws SventonException {
    final SVNRepository repository;
    try {
      repository = SVNRepositoryFactory.create(org.tmatesoft.svn.core.SVNURL.parseURIDecoded(svnUrl.getUrl()));
    } catch (org.tmatesoft.svn.core.SVNException e) {
      throw new SventonException(e.getMessage());
    }
    final File configDirectory = configDirectoryFactory.getConfigDirectoryFor(repositoryName);
    repository.setAuthenticationManager(SVNWCUtil.createDefaultAuthenticationManager(
        configDirectory, credentials.getUserName(), credentials.getPassword(), false));
    repository.setTunnelProvider(SVNWCUtil.createDefaultOptions(true));
    return new SVNKitConnection(repository);
  }

}
