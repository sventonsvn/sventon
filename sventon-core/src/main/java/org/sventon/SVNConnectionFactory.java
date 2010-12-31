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
package org.sventon;

import org.sventon.model.Credentials;
import org.sventon.model.RepositoryName;
import org.sventon.model.SVNURL;

/**
 * Interface for factory responsible for creating subversion repository connections.
 *
 * @author jesper@sventon.org
 */
public interface SVNConnectionFactory {

  /**
   * Gets a repository connection configured using given connection info.
   *
   * @param repositoryName Repository name.
   * @param svnUrl         Subversion repository URL
   * @param credentials    User credentials
   * @return The repository connection.
   * @throws SventonException if unable to create repository connection.
   */
  SVNConnection createConnection(final RepositoryName repositoryName, final SVNURL svnUrl,
                                 final Credentials credentials) throws SventonException;
}
