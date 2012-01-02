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
package org.sventon;

import org.sventon.model.RepositoryName;

import java.io.File;

/**
 * ConfigDirectoryFactory.
 */
public class ConfigDirectoryFactory {

  private final File repositoriesDirectory;

  /**
   * Constructor.
   *
   * @param repositoriesDirectory Repositories root directory
   */
  public ConfigDirectoryFactory(final File repositoriesDirectory) {
    this.repositoriesDirectory = repositoriesDirectory;
  }

  /**
   * Gets the root config directory for given repository instance name.
   *
   * @param repositoryName Name of repository instance
   * @return Root config directory
   */
  public File getConfigDirectoryFor(final RepositoryName repositoryName) {
    return new File(repositoriesDirectory, repositoryName.toString());
  }

}
