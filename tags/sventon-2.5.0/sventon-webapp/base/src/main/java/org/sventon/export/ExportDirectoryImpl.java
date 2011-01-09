/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.export;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sventon.model.RepositoryName;
import org.sventon.model.ZipFileWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Represents a temporary export directory.
 *
 * @author jesper@sventon.org
 */
public final class ExportDirectoryImpl implements ExportDirectory {

  /**
   * Logger for this class.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Date format used for file name assembling.
   */
  public static final String DATE_FORMAT_PATTERN = "yyyyMMddHHmmssSSS";

  /**
   * The directory name prefix.
   */
  private static final String DIRECTORY_PREFIX = "sventon-";

  /**
   * The export directory <code>File</code> instance.
   */
  private final File exportDirectory;

  /**
   * Repository name.
   */
  private final RepositoryName repositoryName;

  /**
   * The charset to use for filenames and comments in compressed archive file.
   */
  private final Charset charset;

  /**
   * UUID identifier for this export directory.
   */
  private final UUID uuid;

  /**
   * Creates an export directory in given parent directory using the name format
   * <code>sventon-[currentTimeMillis] </code>.
   *
   * @param repositoryName Repository name.
   * @param parentDir      Parent dir
   * @param charset        The charset to use for filenames and comments in compressed archive file.
   */
  public ExportDirectoryImpl(final RepositoryName repositoryName, final File parentDir, final Charset charset) {
    this.repositoryName = repositoryName;
    this.charset = charset;
    exportDirectory = new File(parentDir, DIRECTORY_PREFIX + System.currentTimeMillis());
    exportDirectory.deleteOnExit();
    uuid = UUID.randomUUID();
  }

  @Override
  public File compress() throws IOException {
    final File zipFile = new File(exportDirectory.getParentFile(), createTempFilename(new Date()));

    logger.debug("Creating temporary zip file: " + zipFile.getAbsolutePath());
    final ZipFileWrapper zipFileWrapper = new ZipFileWrapper(zipFile, charset);
    zipFileWrapper.add(exportDirectory);
    return zipFileWrapper.getZipFile();
  }

  /**
   * Creates a file name based on repository name, and given timestamp.
   *
   * @param date Timestamp to use as part of the file name.
   * @return Generated file name.
   */
  protected String createTempFilename(final Date date) {
    return repositoryName + "-" + new SimpleDateFormat(DATE_FORMAT_PATTERN).format(date) + ".zip";
  }

  @Override
  public File getDirectory() {
    return exportDirectory;
  }

  @Override
  public void delete() throws IOException {
    FileUtils.forceDelete(exportDirectory);
  }

  @Override
  public UUID getUUID() {
    return uuid;
  }

  @Override
  public boolean mkdirs() {
    return exportDirectory.mkdirs();
  }

  @Override
  public String toString() {
    return "ExportDirectory{" +
        "exportDirectory=" + exportDirectory +
        ", repositoryName='" + repositoryName + '\'' +
        '}';
  }
}
