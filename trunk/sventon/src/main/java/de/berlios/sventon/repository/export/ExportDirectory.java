/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a temporary export directory.
 *
 * @author jesper@users.berlios.de
 */
public final class ExportDirectory {

  /**
   * Logger for this class.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Date format used for file name assembling.
   */
  public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");

  /**
   * The directory name prefix.
   */
  private static final String DIRECTORY_PREFIX = "sventon-";

  /**
   * The export directory <code>File</code> instance.
   */
  private final File exportDirectory;

  /**
   * The instance name.
   */
  private final String instanceName;

  private final Charset charset;

  /**
   * Creates an export directory in given parent directory using the name format
   * <code>sventon-[currentTimeMillis] </code>.
   *
   * @param instanceName Instance name
   * @param parentDir    Parent dir
   * @param charset      The charset to use for filenames and comments in compressed archive file.
   */
  public ExportDirectory(final String instanceName, final File parentDir, final Charset charset) {
    this.instanceName = instanceName;
    this.charset = charset;
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
    new ZipUtils(charset).zipDir(zipFile, exportDirectory);
    return zipFile;
  }

  /**
   * Creates a file name based on instance name, and given timestamp.
   *
   * @param date Timestamp to use as part of the file name.
   * @return Generated file name.
   */
  protected String createTempFilename(final Date date) {
    return instanceName + "-" + DATE_FORMAT.format(date) + ".zip";
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
   * @throws IOException if deletion was unsuccessful.
   */
  public void delete() throws IOException {
    FileUtils.forceDelete(exportDirectory);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ExportDirectory{" +
        "exportDirectory=" + exportDirectory +
        ", instanceName='" + instanceName + '\'' +
        '}';
  }
}
