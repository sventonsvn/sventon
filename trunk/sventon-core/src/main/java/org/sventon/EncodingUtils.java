/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon;

import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

/**
 * Encoding/decoding utility function class.
 */
public final class EncodingUtils {

  /**
   * Private.
   */
  private EncodingUtils() {
  }

  private static final Pattern FORWARD_SLASH_PATTERN = Pattern.compile("%2F");

  /**
   * Default charset, UTF-8.
   */
  private static final String DEFAULT_CHARSET = "UTF-8";

  /**
   * Encodes given string into <tt>application/x-www-form-urlencoded</tt> format using default encoding (UTF-8).
   *
   * @param str String to encode.
   * @return Encoded string.
   */
  public static String encode(final String str) {
    String s = "";
    try {
      s = URLEncoder.encode(str, DEFAULT_CHARSET);
    } catch (UnsupportedEncodingException e) {
      // ignore
    }
    return s;
  }

  /**
   * Encodes given url string using default encoding (UTF-8).
   * Preserves forward slashes.
   *
   * @param url URL string to encode.
   * @return Encoded string with preserved forward slashes.
   */
  public static String encodeUrl(final String url) {
    return FORWARD_SLASH_PATTERN.matcher(encode(url)).replaceAll("/");
  }

  /**
   * URI encodes a path to comply with Subversion specification (according to rfc2396).
   *
   * @param uri Path (or any string) to convert.
   * @return String URL encoded using UTF-8.
   */
  public static String encodeUri(final String uri) {
    try {
      return new URI(null, null, uri, null).toASCIIString();
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Hack to get the correct format of the file name, based on <code>USER-AGENT</code> string.
   * File name will be returned as-is if unable to parse <code>USER-AGENT</code>.
   *
   * @param filename  File name to encode.
   * @param userAgent The request's USER-AGENT string.
   * @return The coded file name.
   */
  public static String encodeFilename(final String filename, final String userAgent) {
    String codedFilename = null;
    try {
      if (null != userAgent && -1 != userAgent.indexOf("MSIE")) {
        codedFilename = URLEncoder.encode(filename, DEFAULT_CHARSET);
      } else if (null != userAgent && -1 != userAgent.indexOf("Mozilla")) {
        codedFilename = MimeUtility.encodeText(filename, DEFAULT_CHARSET, "B");
      }
    } catch (UnsupportedEncodingException uee) {
      // Silently ignore
    }
    return codedFilename != null ? codedFilename : filename;
  }

}
