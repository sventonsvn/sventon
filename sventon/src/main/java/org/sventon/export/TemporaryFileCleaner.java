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
package org.sventon.export;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Temporary export file cleaner.
 *
 * @author jesper@sventon.org
 */
public final class TemporaryFileCleaner {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Temp file root directory.
   */
  private File directory;

  /**
   * File name filter to use.
   */
  private FilenameFilter filenameFilter;

  /**
   * Expiration rule.
   */
  private ExpirationRule expirationRule;

  /**
   * Constructor.
   *
   * @param directory      The directory.
   * @param filenameFilter Filter to use.
   * @param expirationRule Expiration rule.
   */
  public TemporaryFileCleaner(final File directory, final FilenameFilter filenameFilter, final ExpirationRule expirationRule) {
    Validate.isTrue(directory.exists(), "Directory does not exist: " + directory);
    Validate.isTrue(directory.isDirectory(), "Not a directory: " + directory);
    Validate.notNull(filenameFilter);
    Validate.notNull(expirationRule);
    this.directory = directory;
    this.filenameFilter = filenameFilter;
    this.expirationRule = expirationRule;
  }

  /**
   * Cleans the given directory.
   * <p/>
   * All filenames matching <code>sventon-[millis].zip</code>
   * older than given threshold value will be deleted.
   */
  public void clean() {
    for (final File file : directory.listFiles(filenameFilter)) {
      if (expirationRule.hasExpired(file)) {
        logger.debug("Deleting tempfile [" + file.getAbsolutePath() + "]");
        file.delete();
      }
    }
  }

}
