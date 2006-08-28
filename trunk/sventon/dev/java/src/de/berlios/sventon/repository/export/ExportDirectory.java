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

import de.berlios.sventon.util.ZipUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a temporary export directory.
 *
 * @author jesper@users.berlios.de
 */
public class ExportDirectory {

  /**
   * Logger for this class.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Date format used for file name assembling.
   */
  public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");

  /**
   * The export directory <code>File</code> instance.
   */
  private final File exportDirectory;

  /**
   * The directory name prefix.
   */
  private static final String DIRECTORY_PREFIX = "sventon-";

  /**
   * The instance name.
   */
  private String instanceName;

  /**
   * Creates an export directory in given parent directory using the name format
   * <code>sventon-[currentTimeMillis] </code>.
   *
   * @param instanceName Instance name
   * @param parentDir    Parent dir
   */
  public ExportDirectory(final String instanceName, final String parentDir) {
    this.instanceName = instanceName;
    exportDirectory = new File(parentDir, DIRECTORY_PREFIX + System.currentTimeMillis());
    exportDirectory.mkdirs();
    exportDirectory.deleteOnExit();
  }

  /**
   * Compresses the temporary export directory.
   *
   * @return The <code>File</code> instance of the compressed file.
   * @throws IOException if IO error occurs.
   */
  public File compress() throws IOException {
    final File zipFile = new File(exportDirectory.getParentFile(), createTempFilename(new Date()));

    logger.debug("Creating temporary zip file: " + zipFile.getAbsolutePath());
    new ZipUtils().zipDir(zipFile, exportDirectory);
    return zipFile;
  }

  /**
   * Creates a file name based on instance name, current date and time.
   *
   * @param now Current time
   * @return Generated file name.
   */
  protected String createTempFilename(final Date now) {
    return instanceName + "-" + DATE_FORMAT.format(now) + ".zip";
  }

  /**
   * Gets the export directory's <code>File</code> instance.
   *
   * @return The File instance.
   */
  public File getFile() {
    return exportDirectory;
  }

  /**
   * Deletes the temporary export directory.
   *
   * @return True if deletion was successful, false if not.
   */
  public boolean delete() {
    return deleteDir(exportDirectory);
  }

  /**
   * Deletes a directory recursively.
   *
   * @param dir Directory to delete
   * @return True if directory was successfully deleted.
   */
  private static boolean deleteDir(final File dir) {
    if (dir.isDirectory()) {
      for (String child : dir.list()) {
        final boolean success = deleteDir(new File(dir, child));
        if (!success) {
          return false;
        }
      }
    }
    return dir.delete();
  }

}
