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
