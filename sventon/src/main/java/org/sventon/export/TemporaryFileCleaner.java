/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
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
import org.sventon.appl.ConfigDirectory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

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
   * @param configDirectory The configuration directory.
   * @param filenameFilter  Filter to use.
   * @param expirationRule  Expiration rule.
   */
  public TemporaryFileCleaner(final ConfigDirectory configDirectory, final FilenameFilter filenameFilter, final ExpirationRule expirationRule) {
    final File exportDirectory = configDirectory.getExportDirectory();
    Validate.isTrue(exportDirectory.exists(), "Directory does not exist: " + exportDirectory.getAbsolutePath());
    Validate.isTrue(exportDirectory.isDirectory(), "Not a directory: " + exportDirectory.getAbsolutePath());
    Validate.notNull(filenameFilter);
    Validate.notNull(expirationRule);
    this.directory = exportDirectory;
    this.filenameFilter = filenameFilter;
    this.expirationRule = expirationRule;
  }

  /**
   * Cleans the given directory.
   * <p/>
   * All filenames matching <code>sventon-[millis].zip</code>
   * older than given threshold value will be deleted.
   *
   * @throws IOException if unable to delete temporary files.
   */
  public void clean() throws IOException {
    for (final File file : directory.listFiles(filenameFilter)) {
      if (expirationRule.hasExpired(file)) {
        logger.debug("Deleting tempfile [" + file.getAbsolutePath() + "]");
        if (!file.delete()) {
          throw new IOException("Unable to delete: " + file.getAbsolutePath());
        }
      }
    }
  }

}
