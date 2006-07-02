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
   * @return If directory was successfully deleted.
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
  public static synchronized File generateTempDir(final String parentDir) {
    final File tempDir = new File(parentDir, "sventon-" + System.currentTimeMillis());
    tempDir.mkdirs();
    return tempDir;
  }
}
