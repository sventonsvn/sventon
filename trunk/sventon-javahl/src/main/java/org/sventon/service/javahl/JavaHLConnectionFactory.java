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

import org.sventon.SVNConnection;
import org.sventon.SVNConnectionFactory;
import org.sventon.SventonException;
import org.sventon.model.Credentials;
import org.sventon.model.RepositoryName;
import org.sventon.model.SVNURL;

/**
 * JavaHLConnectionFactory.
 *
 * @author jesper@sventon.org
 */
public class JavaHLConnectionFactory implements SVNConnectionFactory {
  @Override
  public SVNConnection createConnection(RepositoryName repositoryName, SVNURL svnUrl, Credentials credentials) throws SventonException {
    throw new UnsupportedOperationException();
  }
}
