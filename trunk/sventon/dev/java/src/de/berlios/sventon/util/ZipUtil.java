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

import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for handling ZIP actions.
 *
 * @author jesper@users.berlios.de
 */
public class ZipUtil {

  /**
   * Zips the given directory recursively.
   *
   * @param directory The directory to zip.
   * @param zos       The output stream to write to.
   * @throws IllegalArgumentException if <code>directory</code> isn't a directory.
   */
  public void zipDir(final File directory, final ZipOutputStream zos) {
    zipDirInternal(directory, directory, zos);
  }

  /**
   * Zips the given directory recursively.
   *
   * @param baseDir   Base directory root to zip.
   * @param directory The directory to zip.
   * @param zos       The output stream to write to.
   * @throws IllegalArgumentException if <code>directory</code> isn't a directory.
   */
  protected void zipDirInternal(final File baseDir, final File directory, final ZipOutputStream zos) {
    try {
      if (!directory.isDirectory()) {
        throw new IllegalArgumentException("Argument is not a directory: " + directory);
      }

      final String[] dirList = directory.list();
      final byte[] readBuffer = new byte[4096];
      int bytesIn = 0;

      for (String aDirList : dirList) {
        final File fileEntry = new File(directory, aDirList);
        if (fileEntry.isDirectory()) {
          //add directory contents recursively
          zipDirInternal(baseDir, fileEntry, zos);
          continue;
        }
        final FileInputStream fis = new FileInputStream(fileEntry);
        final String entryName = createZipEntryName(baseDir.getAbsolutePath(), fileEntry.getAbsolutePath());
        System.out.println("entryName = " + entryName);
        final ZipEntry anEntry = new ZipEntry(entryName);
        zos.putNextEntry(anEntry);
        while ((bytesIn = fis.read(readBuffer)) != -1) {
          zos.write(readBuffer, 0, bytesIn);
        }
        fis.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Zips the given file.
   *
   * @param file The file to zip.
   * @param zos  The output stream to write to.
   */
  public void zipFile(final File file, final ZipOutputStream zos) {
    try {
      if (!file.isFile()) {
        throw new IllegalArgumentException("Argument is not a file: " + file);
      }

      final byte[] readBuffer = new byte[2156];
      int bytesIn = 0;

      final FileInputStream fis = new FileInputStream(file);
      final ZipEntry anEntry = new ZipEntry(file.getName());
      zos.putNextEntry(anEntry);
      while ((bytesIn = fis.read(readBuffer)) != -1) {
        zos.write(readBuffer, 0, bytesIn);
      }
      fis.close();

    } catch (Exception e) {
      e.printStackTrace();
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