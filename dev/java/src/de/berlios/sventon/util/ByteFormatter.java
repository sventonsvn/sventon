package de.berlios.sventon.util;

import java.text.DecimalFormat;
import java.text.FieldPosition;

/**
 * Formatter for byte size values.
 *
 * @author jesper@users.berlios.de  
 */
public final class ByteFormatter {

  public static final long s1kB = 1024L;
  public static final long s1MB = 1024L * 1024L;
  public static final long s1GB = 1024L * 1024L * 1024L;
  public static final long s1TB = 1024L * 1024L * 1024L * 1024L;

  /**
   * Formats given byte size value.
   * <br>
   * Examples:
   * <table>
   *   <tr>
   *     <th>Input</th>
   *     <th>Output</th>
   *   </tr>
   *   <tr>
   *     <td>1000</td>
   *     <td>1000</td>
   *   </tr>
   *   <tr>
   *     <td>1200</td>
   *     <td>1 kB</td>
   *   </tr>
   *   <tr>
   *     <td>123456</td>
   *     <td>120 kB</td>
   *   </tr>
   *   <tr>
   *     <td>12345678</td>
   *     <td>11,77 MB</td>
   *   </tr>
   *   <tr>
   *     <td>1234567890</td>
   *     <td>1,15 GB</td>
   *   </tr>
   * </table>
   * @param size Byte size to format
   * @return The formatted string.
   */
  public static String format(long size)
  {
    StringBuffer buffer = new StringBuffer(16);
    DecimalFormat byteFormat = new DecimalFormat("0.00");

    if (size < s1kB) {
      buffer.append(size);
    }
    else if (size < s1MB) {
      int value = (int)(size) >> 10;
      buffer.append(value).append(" kB");
    }
    else if (size < s1GB) {
      double d = ((double)size) / s1MB;
      byteFormat.format(d, buffer, new FieldPosition(0)).append(" MB");
    }
    else if (size < s1TB) {
      double d = ((double)size) / s1GB;
      byteFormat.format(d, buffer, new FieldPosition(0)).append(" GB");
    } else {
      double d = ((double)size) / s1TB;
      byteFormat.format(d, buffer, new FieldPosition(0)).append(" TB");
    }
    return buffer.toString();
  }
}
