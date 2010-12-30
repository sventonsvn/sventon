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

import org.sventon.ConfigDirectoryFactory;
import org.sventon.SVNConnectionFactory;
import org.sventon.service.RepositoryService;
import org.sventon.service.SVNProvider;

/**
 * SVN provider based on JavaHL.
 */
public class JavaHLSVNProvider extends SVNProvider {

  @Override
  public SVNConnectionFactory getConnectionFactory(final ConfigDirectoryFactory configDirectoryFactory) {
    return new JavaHLConnectionFactory(configDirectoryFactory);
  }

  @Override
  public RepositoryService getRepositoryService() {
    return new JavaHLRepositoryService();
  }

}
