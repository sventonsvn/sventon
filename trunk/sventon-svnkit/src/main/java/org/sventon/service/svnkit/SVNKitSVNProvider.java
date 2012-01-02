/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.service.svnkit;

import org.sventon.ConfigDirectoryFactory;
import org.sventon.SVNConnectionFactory;
import org.sventon.service.RepositoryService;
import org.sventon.service.SVNProvider;

/**
 * SVN provider based on SVNKit.
 */
public class SVNKitSVNProvider extends SVNProvider {

  @Override
  public SVNConnectionFactory getConnectionFactory(final ConfigDirectoryFactory configDirectoryFactory) {
    return new SVNKitConnectionFactory(configDirectoryFactory);
  }

  @Override
  public RepositoryService getRepositoryService() {
    return new SVNKitRepositoryService();
  }

}
