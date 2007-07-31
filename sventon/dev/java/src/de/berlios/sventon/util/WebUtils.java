/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Web related utility methods.
 *
 * @author jesper@users.berlios.de
 */
public final class WebUtils {

  public static final String NL_REGEXP = "(\r\n|\r|\n|\n\r)";

  private WebUtils() {
  }

  /**
   * Replaces any new line characters with a HTML BR.
   *
   * @param string String to replace
   * @return Result
   */
  public static String nl2br(final String string) {
    return string.replaceAll(NL_REGEXP, "<br/>");
  }

  /**
   * Extracts the full request URL, including scheme, server name,
   * server port (if not 80) and context path.
   *
   * @param request The request.
   * @return The full URL, ending with a forward slash (/).
   */
  public static String extractBaseURLFromRequest(final HttpServletRequest request) {
    final StringBuilder sb = new StringBuilder();
    sb.append(request.getScheme());
    sb.append("://");
    sb.append(request.getServerName());
    if (request.getServerPort() != 80) {
      sb.append(":");
      sb.append(request.getServerPort());
    }
    sb.append(request.getContextPath());
    sb.append("/");
    return sb.toString();
  }
}
