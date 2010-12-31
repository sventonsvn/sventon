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
package org.sventon.util;

/**
 * Utility class for path handling.
 *
 * @author jesper@sventon.org
 */
public final class PathUtil {

  /**
   * Private.
   */
  private PathUtil() {
  }

  /**
   * Gets the target (leaf/end) part of the <code>path</code>, it could be a file
   * or a directory.
   * <p/>
   * The returned string will have no final "/", even if it is a directory.
   *
   * @param fullpath Path
   * @return Target part of the path. If <tt>fullpath</tt> is null an
   *         empty string will be returned.
   */
  public static String getTarget(final String fullpath) {
    if (fullpath == null) {
      return "";
    }
    final String[] splitString = fullpath.split("/");
    final int length = splitString.length;
    if (length == 0) {
      return "";
    } else {
      return splitString[splitString.length - 1];
    }
  }

}
