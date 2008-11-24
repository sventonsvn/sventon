/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.appl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.File;

/**
 * ConfigDirectory.
 *
 * @author jesper@sventon.org
 */
public class ConfigDirectory implements ServletContextAware {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * The servlet context.
   */
  private ServletContext servletContext;

  private File configRootDirectory = null;
  private final File configDirectoryLocation;

  private final String exportDirectoryName;
  private File exportDirectory;

  private final String repositoriesDirectoryName;
  private File repositoriesDirectory;

  /**
   * Directory creation flag, default set to true.
   */
  private boolean createDirectories = true;

  /**
   * Constructor.
   *
   * @param configDirectoryLocation   Location where to store the sventon config root directory,
   *                                  usually <tt>java.io.tmpdir/sventon_temp/</tt>.
   * @param exportDirectoryName       Directory name for the export.
   * @param repositoriesDirectoryName Directory name for the configured repositories.
   */
  public ConfigDirectory(final File configDirectoryLocation, final String exportDirectoryName,
                         final String repositoriesDirectoryName) {
    this.configDirectoryLocation = configDirectoryLocation;
    this.exportDirectoryName = exportDirectoryName;
    this.repositoriesDirectoryName = repositoriesDirectoryName;
  }

  /**
   * Sets the directory creation flag.
   * Used for testing purposes.
   *
   * @param createDirectories True or false.
   */
  public void setCreateDirectories(final boolean createDirectories) {
    this.createDirectories = createDirectories;
  }

  /**
   * {@inheritDoc}
   */
  public void setServletContext(final ServletContext servletContext) {
    this.servletContext = servletContext;

    configRootDirectory = new File(configDirectoryLocation, servletContext.getContextPath());
    exportDirectory = new File(getConfigRootDirectory(), exportDirectoryName);
    repositoriesDirectory = new File(getConfigRootDirectory(), repositoriesDirectoryName);

    if (createDirectories) {
      configRootDirectory.mkdirs();
      exportDirectory.mkdirs();
      repositoriesDirectory.mkdirs();
    }

    logger.info("Config root directory set to: " + configRootDirectory.getAbsolutePath());
  }

  /**
   * Gets the sventon config root directory,
   * i.e. {@link #configDirectoryLocation} + {@link javax.servlet.ServletContext#getContextPath()}.
   *
   * @return sventon config root directory.
   * @throws IllegalStateException if ServletContext has not been set.
   */
  public File getConfigRootDirectory() {
    assertServletContextSet();
    return configRootDirectory;
  }

  /**
   * Gets the export directory.
   *
   * @return The export directory
   */
  public File getExportDirectory() {
    assertServletContextSet();
    return exportDirectory;
  }

  /**
   * Gets the repositories directory.
   *
   * @return Repositories directory.
   */
  public File getRepositoriesDirectory() {
    assertServletContextSet();
    return repositoriesDirectory;
  }

  private void assertServletContextSet() {
    if (servletContext == null) {
      throw new IllegalStateException("ServletContext has not been set!");
    }
  }
}
