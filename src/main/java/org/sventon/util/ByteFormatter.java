/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.util;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Formatter for byte size values.
 *
 * @author jesper@sventon.org
 */
public final class ByteFormatter {

  private static final long s1kB = 1024L;
  private static final long s1MB = 1024L * 1024L;
  private static final long s1GB = 1024L * 1024L * 1024L;
  private static final long s1TB = 1024L * 1024L * 1024L * 1024L;

  /**
   * Private - not supposed to instantiate.
   */
  private ByteFormatter() {
  }

  /**
   * Formats given byte size value.
   * <br>
   * Examples:
   * <table>
   * <tr>
   * <th>Input</th>
   * <th>Output</th>
   * </tr>
   * <tr>
   * <td>1000</td>
   * <td>1000</td>
   * </tr>
   * <tr>
   * <td>1200</td>
   * <td>1 kB</td>
   * </tr>
   * <tr>
   * <td>123456</td>
   * <td>120 kB</td>
   * </tr>
   * <tr>
   * <td>12345678</td>
   * <td>11,77 MB</td>
   * </tr>
   * <tr>
   * <td>1234567890</td>
   * <td>1,15 GB</td>
   * </tr>
   * </table>
   *
   * @param size   Byte size to format
   * @param locale Locale to use for formatting
   * @return The formatted string.
   */
  public static String format(final long size, final Locale locale) {
    final StringBuffer buffer = new StringBuffer();
    final NumberFormat byteFormat = NumberFormat.getNumberInstance(locale);

    byteFormat.setMaximumFractionDigits(2);
    byteFormat.setMinimumFractionDigits(2);

    if (size < s1kB) {
      buffer.append(size);
    } else if (size < s1MB) {
      int value = (int) (size) >> 10;
      buffer.append(value).append(" kB");
    } else if (size < s1GB) {
      double d = ((double) size) / s1MB;
      byteFormat.format(d, buffer, new FieldPosition(0)).append(" MB");
    } else if (size < s1TB) {
      double d = ((double) size) / s1GB;
      byteFormat.format(d, buffer, new FieldPosition(0)).append(" GB");
    } else {
      double d = ((double) size) / s1TB;
      byteFormat.format(d, buffer, new FieldPosition(0)).append(" TB");
    }
    return buffer.toString();
  }
}
