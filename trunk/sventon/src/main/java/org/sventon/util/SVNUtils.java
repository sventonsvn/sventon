/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
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
 * Misc SVN utilities.
 *
 * @author jesper@sventon.org
 */
public final class SVNUtils {

  /**
   * Private.
   */
  private SVNUtils() {
  }

  /**
   * Check to see if the given string can be considered as describing a text MIME type
   *
   * @param mimeType the MIME type
   * @return true if given string describes a MIME type, otherwise false.
   */
  public static boolean isTextMimeType(String mimeType) {
    return (mimeType == null || mimeType.startsWith("text/"));
  }
}
