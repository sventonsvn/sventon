/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
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

    final String[] splittedString = fullpath.split("/");
    final int length = splittedString.length;
    if (length == 0) {
      return "";
    } else {
      return splittedString[splittedString.length - 1];
    }
  }

  /**
   * Gets the path, excluding the target leaf.
   * <p/>
   * The returned string will have a final "/". If the path info is empty or null, ""
   * (empty string) will be returned.
   *
   * @param fullpath Path, e.g. <tt>/trunk/src/File.java</tt>
   * @return Path excluding taget leaf, e.g. <tt>/trunk/src/</tt>
   */
  public static String getPathPart(final String fullpath) {
    if (fullpath == null) {
      return "";
    }

    final int lastIndex = fullpath.lastIndexOf('/');
    if (lastIndex == -1) {
      return "";
    } else {
      return fullpath.substring(0, lastIndex) + "/";
    }
  }

  /**
   * Gets the path, excluding the end/leaf.
   * <p/>
   * The returned string will have a final "/". If the path info is empty or null, ""
   * (empty string) will be returned.
   *
   * @param fullpath Path
   * @return Path excluding taget (end/leaf).
   */
  public static String getPathNoLeaf(final String fullpath) {
    if (fullpath == null) {
      return "";
    }

    String work = fullpath;
    if (work.endsWith("/")) {
      work = work.substring(0, work.length() - 1);
    }

    final int lastIndex = work.lastIndexOf('/');
    if (lastIndex == -1) {
      return "";
    } else {
      return work.substring(0, lastIndex) + "/";
    }
  }
}
