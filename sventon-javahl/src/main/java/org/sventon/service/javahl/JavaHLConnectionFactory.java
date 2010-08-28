/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.service.javahl;

import org.apache.commons.lang.Validate;
import org.sventon.SVNConnection;
import org.sventon.SVNConnectionFactory;
import org.sventon.SventonException;
import org.sventon.appl.ConfigDirectory;
import org.sventon.model.Credentials;
import org.sventon.model.RepositoryName;
import org.sventon.model.SVNURL;
import org.tigris.subversion.javahl.ClientException;
import org.tigris.subversion.javahl.SVNClient;

import java.io.File;

/**
 * JavaHLConnectionFactory.
 *
 * @author jesper@sventon.org
 */
public class JavaHLConnectionFactory implements SVNConnectionFactory {

  private final ConfigDirectory configurationDirectory;

  /**
   * Constructor.
   *
   * @param configurationDirectory Where to place the subversion config files.
   */
  public JavaHLConnectionFactory(ConfigDirectory configurationDirectory) {
    Validate.notNull(configurationDirectory, "Configuration directory cannot be null!");
    this.configurationDirectory = configurationDirectory;
  }

  @Override
  public SVNConnection createConnection(RepositoryName repositoryName, SVNURL svnUrl, Credentials credentials) throws SventonException {
    final File configDirectory = configurationDirectory.getConfigDirectoryFor(repositoryName);
    final SVNClient client = new SVNClient();
    try {
      client.setConfigDirectory(configDirectory.getAbsolutePath());
    } catch (ClientException ce) {
      throw new SventonException("Unable to create SVN connection", ce);
    }
    return new JavaHLConnection(client, svnUrl, credentials);
  }

}
