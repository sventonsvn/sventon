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
package de.berlios.sventon.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for handling ZIP actions.
 *
 * @author jesper@users.berlios.de
 */
public final class ZipUtil {

  /**
   * Logger for this class and subclasses.
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * Zips the given directory recursively.
   *
   * @param directory The directory to zip.
   * @param zos       The output stream to write to.
   * @throws IllegalArgumentException if <code>directory</code> isn't a directory.
   * @throws IOException              if IO error occurs
   */
  public void zipDir(final File directory, final ZipOutputStream zos) throws IOException {
    logger.debug("Zipping directory: " + directory.getAbsolutePath());
    zipDirInternal(directory, directory, zos);
  }

  /**
   * Zips the given directory recursively.
   *
   * @param baseDir   Base directory root to zip.
   * @param directory The directory to zip.
   * @param zos       The output stream to write to.
   * @throws IllegalArgumentException if <code>directory</code> isn't a directory.
   * @throws IOException              if IO error occurs
   */
  protected void zipDirInternal(final File baseDir, final File directory, final ZipOutputStream zos) throws IOException {
    if (!directory.isDirectory()) {
      throw new IllegalArgumentException("Argument is not a directory: " + directory);
    }

    FileInputStream input = null;
    final String[] dirList = directory.list();
    final byte[] readBuffer = new byte[4096];
    int bytesIn = 0;

    try {
      for (String aDirList : dirList) {
        final File fileEntry = new File(directory, aDirList);
        if (fileEntry.isDirectory()) {
          //add directory contents recursively
          zipDirInternal(baseDir, fileEntry, zos);
          continue;
        }
        input = new FileInputStream(fileEntry);
        final String entryName = createZipEntryName(baseDir.getAbsolutePath(), fileEntry.getAbsolutePath());
        final ZipEntry anEntry = new ZipEntry(entryName);
        logger.debug("Adding ZipEntry: " + entryName);
        zos.putNextEntry(anEntry);
        while ((bytesIn = input.read(readBuffer)) != -1) {
          zos.write(readBuffer, 0, bytesIn);
        }
      }
    } finally {
      if (input != null) {
        input.close();
      }
    }
  }

  /**
   * Zips the given file.
   *
   * @param file The file to zip.
   * @param zos  The output stream to write to.
   * @throws IOException if IO error occurs
   */
  public void zipFile(final File file, final ZipOutputStream zos) throws IOException {

    if (!file.isFile()) {
      throw new IllegalArgumentException("Argument is not a file: " + file);
    }

    FileInputStream input = null;
    try {
      final byte[] readBuffer = new byte[2156];
      int bytesIn = 0;
      input = new FileInputStream(file);
      final ZipEntry anEntry = new ZipEntry(file.getName());
      zos.putNextEntry(anEntry);
      while ((bytesIn = input.read(readBuffer)) != -1) {
        zos.write(readBuffer, 0, bytesIn);
      }
    } finally {
      if (input != null) {
        input.close();
      }
    }
  }

  /**
   * Creates a zip entry name.
   *
   * @param basePath Base path to remove from given <code>path</code>.
   * @param path     The path and file name to the zip entry.
   * @return The stripped path used to store entry in zip file.
   */
  protected String createZipEntryName(final String basePath, final String path) {
    return (path.substring(basePath.length(), path.length()));
  }

}