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

import javax.servlet.ServletOutputStream;
import java.io.*;

/**
 * File and directory utility class.
 *
 * @author jesper@users.berlios.de
 */
public final class FileUtils {

  /**
   * Deletes a directory recursively.
   *
   * @param dir Directory to delete
   * @return True if directory was successfully deleted.
   */
  public static boolean deleteDir(final File dir) {
    if (dir.isDirectory()) {
      for (String child : dir.list()) {
        final boolean success = FileUtils.deleteDir(new File(dir, child));
        if (!success) {
          return false;
        }
      }
    }
    return dir.delete();
  }

  /**
   * Creates a temporary sub directory to given parent directory.
   * The directory name will be in the format <code>sventon-[currentTimeMillis]</code>.
   *
   * @param parentDir Parent directory
   * @return The temporary directory
   */
  public static synchronized File createTempDir(final String parentDir) {
    final File tempDir = new File(parentDir, "sventon-" + System.currentTimeMillis());
    tempDir.mkdirs();
    return tempDir;
  }

  /**
   * Reads the given input stream and writes the buffered contents to the output.
   *
   * @param input  Input (source) stream
   * @param output Output (destination) stream
   * @throws IOException If unable to write to output stream.
   */
  public static void writeStream(final InputStream input, final ServletOutputStream output) throws IOException {
    byte[] buffer = new byte[8192];
    int bytesRead;
    while ((bytesRead = input.read(buffer)) >= 0) {
      output.write(buffer, 0, bytesRead);
    }
  }

  /**
   * Closes given <code>Closeable</code> instance.
   * <b>Exceptions will be ignored.</b>
   *
   * @param closable
   */
  public static void close(final Closeable closable) {
    if (closable != null) {
      try {
        closable.close();
      } catch (IOException e) {
        // ignore
      }
    }
  }
}
