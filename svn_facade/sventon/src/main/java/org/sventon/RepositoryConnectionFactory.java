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
package org.sventon;

import org.sventon.model.Credentials;
import org.sventon.model.RepositoryName;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * Interface for factory responsible for creating subversion repository connections.
 *
 * @author jesper@sventon.org
 */
public interface RepositoryConnectionFactory {

  /**
   * Gets a repository connection configured using given connection info.
   *
   * @param repositoryName Repository name.
   * @param svnUrl         Subversion repository URL
   * @param credentials    User credentials
   * @return The repository connection.
   * @throws SVNException if unable to create repository connection.
   */
  SVNConnection createConnection(final RepositoryName repositoryName, final SVNURL svnUrl,
                                 final Credentials credentials) throws SVNException;
}
