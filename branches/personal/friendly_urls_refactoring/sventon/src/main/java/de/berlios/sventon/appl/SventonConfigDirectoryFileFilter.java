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
package de.berlios.sventon.appl;

import org.apache.commons.lang.Validate;

import java.io.File;
import java.io.FileFilter;

/**
 * File filter that accepts sventon configuration directories.
 */
public class SventonConfigDirectoryFileFilter implements FileFilter {

  /**
   * The name of the sventon configuration file.
   */
  private String configurationFilename;

  /**
   * Constructor.
   *
   * @param configurationFilename Name of the sventon configuration file.
   */
  public SventonConfigDirectoryFileFilter(final String configurationFilename) {
    Validate.notEmpty(configurationFilename, "Config filename cannot be empty");
    this.configurationFilename = configurationFilename;
  }

  /**
   * Checks to see if the file is a sventon configuration directory.
   *
   * @param pathname the File to check
   * @return true if the file is a sventon configuration directory.
   */
  public boolean accept(final File pathname) {
    return pathname.isDirectory() && new File(pathname, configurationFilename).exists();
  }
}
