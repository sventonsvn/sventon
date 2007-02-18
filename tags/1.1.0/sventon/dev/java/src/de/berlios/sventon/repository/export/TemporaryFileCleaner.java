/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.repository.export;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Temporary export file cleaner.
 *
 * @author jesper@users.berlios.de
 */
public class TemporaryFileCleaner {

  /**
   * The logging instance.
   */
  protected final Log logger = LogFactory.getLog(getClass());

  private File exportRootDir;
  private long timeThreshold;
  private Pattern digitPattern = Pattern.compile("\\d+");

  /**
   * Sets the export root directory.
   *
   * @param exportRootDir Directory
   */
  public void setExportRootDir(final File exportRootDir) {
    this.exportRootDir = exportRootDir;
    if (!this.exportRootDir.exists()) {
      throw new IllegalArgumentException("Directory does not exist: " + exportRootDir);
    }
    if (!this.exportRootDir.isDirectory()) {
      throw new IllegalArgumentException("Not a directory: " + exportRootDir);
    }
  }

  /**
   * Sets the time threshold (in milliseconds).
   * Files older than given threshold will be deleted next time
   * {@link #clean()} is invoked.
   *
   * @param timeThreshold Time in milliseconds.
   */
  public void setTimeThreshold(long timeThreshold) {
    this.timeThreshold = timeThreshold;
  }

  /**
   * Cleans the export directory.
   * All filenames matching <code>sventon-[millis].zip</code>
   * older than given threshold value will be deleted.
   */
  public void clean() {
    for (final File file : exportRootDir.listFiles(new ExportFileFilter())) {
      if (isOld(file)) {
        logger.debug("Deleting tempfile [" + file.getAbsolutePath() + "]");
        file.delete();
      }
    }
  }

  /**
   * Returns true if this file is old enough to be deleted
   *
   * @param tempFile Temporary file
   * @return True if file is old enough, according to the threshold value.
   */
  protected boolean isOld(final File tempFile) {
    final Matcher matcher = digitPattern.matcher(tempFile.getName());
    matcher.find();
    try {
      final Date fileDate = ExportDirectory.DATE_FORMAT.parse(matcher.group());
      return System.currentTimeMillis() - fileDate.getTime() > timeThreshold;
    } catch (ParseException pe) {
      logger.warn("Unable to parse date part of filename: " + tempFile.getName());
      return false;
    }
  }

}
