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
package org.sventon.appl;

import org.apache.commons.lang.StringUtils;
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

  public static final String PROPERTY_KEY_SVENTON_DIR_SYSTEM = "sventon.dir";

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * The servlet context.
   */
  private ServletContext servletContext;

  private final String sventonDirProperty = System.getProperty(PROPERTY_KEY_SVENTON_DIR_SYSTEM);
  private final String exportDirectoryName;
  private final String repositoriesDirectoryName;

  private File sventonConfigDirectory;
  private File configRootDirectory = null;
  private File exportDirectory;
  private File repositoriesDirectory;

  /**
   * Directory creation flag, default set to true.
   */
  private boolean createDirectories = true;

  /**
   * Constructor.
   *
   * @param sventonConfigDirectory    Location where to store the sventon config root directory,
   *                                  usually <tt>java.io.tmpdir/sventon_config/</tt>.
   *                                  This parameter can be overridden by setting the system property
   *                                  <tt>sventon.dir</tt> to a user preferred directory.
   * @param exportDirectoryName       Directory name for the export.
   * @param repositoriesDirectoryName Directory name for the configured repositories.
   */
  public ConfigDirectory(final File sventonConfigDirectory, final String exportDirectoryName,
                         final String repositoriesDirectoryName) {

    this.sventonConfigDirectory = sventonConfigDirectory;
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
    handleConfigDirectoryOverride();

    String contextPath;
    try {
      contextPath = servletContext.getContextPath();
    } catch (NoSuchMethodError e) {
      // For backwards compatibility, simply set to "svn"
      logger.info("Method ServletContext.getContextPath() is not supported by your servlet container. " +
          "Defaulting to [svn].");
      contextPath = "svn";
    }

    configRootDirectory = new File(sventonConfigDirectory, contextPath);
    exportDirectory = new File(getConfigRootDirectory(), exportDirectoryName);
    repositoriesDirectory = new File(getConfigRootDirectory(), repositoriesDirectoryName);

    if (createDirectories) {
      createDirectoryStructure();
    }
    logger.info("Config root directory for current servlet context set to: " + configRootDirectory.getAbsolutePath());
  }

  private void createDirectoryStructure() {
    if ((!configRootDirectory.exists() && !configRootDirectory.mkdirs()) ||
        (!exportDirectory.exists() && !exportDirectory.mkdirs()) ||
        (!repositoriesDirectory.exists() && !repositoriesDirectory.mkdirs())) {
      throw new RuntimeException("Unable to create directory structure: " + configRootDirectory.getAbsolutePath());
    }
  }

  private void handleConfigDirectoryOverride() {
    if (StringUtils.isNotBlank(sventonDirProperty)) {
      sventonConfigDirectory = new File(sventonDirProperty);
      logger.info("sventon config root directory (overridden by system property) set to: " + sventonConfigDirectory);
    }
  }

  /**
   * Gets the sventon config root directory,
   * i.e. {@link #sventonConfigDirectory} + {@link javax.servlet.ServletContext#getContextPath()}.
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
