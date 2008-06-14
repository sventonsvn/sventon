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
package de.berlios.sventon.util;


import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * Web related utility methods.
 *
 * @author jesper@users.berlios.de
 */
public final class WebUtils {

  public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
  public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain; charset=\"UTF-8\"";

  /**
   * Content disposition Response header.
   */
  public static final String CONTENT_DISPOSITION_HEADER = "Content-disposition";

  public static final String BR = System.getProperty("line.separator");
  public static final String NBSP = "&nbsp;";
  public static final Pattern NL_REGEXP = Pattern.compile("(\r\n|\r|\n|\n\r)");

  /**
   * Private constructor to prevent instantiation.
   */
  private WebUtils() {
  }

  /**
   * Replaces any new line characters with a HTML BR.
   *
   * @param string String to replace
   * @return Result, or <tt>null</tt> if given input was <tt>null</tt>.
   */
  public static String nl2br(final String string) {
    if (StringUtils.isEmpty(string)) {
      return string;
    }
    return NL_REGEXP.matcher(string).replaceAll("<br>");
  }

  /**
   * Replaces leading spaces with the HTML entity <code>&nbsp;</code>.
   *
   * @param string Input string. Can be one or more lines.
   * @return Replaced output string.
   */
  public static String replaceLeadingSpaces(final String string) {
    if (StringUtils.isEmpty(string)) {
      return string;
    }

    final StringBuilder sb = new StringBuilder();
    final String[] lines = NL_REGEXP.split(string);

    for (final String line : lines) {
      if (!StringUtils.isWhitespace(line)) {
        String result = org.springframework.util.StringUtils.trimLeadingWhitespace(line);

        int removedSpacesCount = line.length() - result.length();
        for (int i = 0; i < removedSpacesCount; i++) {
          result = NBSP + result;
        }
        sb.append(result);
      } else {
        sb.append(line);
      }
      // Make sure to only append NEWLINE when multiple lines.
      if (lines.length > 1) {
        sb.append(BR);
      }
    }
    return sb.toString();
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

  /**
   * Extracts the servlet name from given request.
   *
   * @param request Request
   * @return Servlet name.
   */
  public static String extractServletNameFromRequest(final HttpServletRequest request) {
    final String servletPath = request.getServletPath();
    if (servletPath != null) {
      return servletPath.substring(servletPath.lastIndexOf("/") + 1);
    }
    return null;
  }
}
