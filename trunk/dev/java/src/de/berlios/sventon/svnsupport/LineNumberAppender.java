package de.berlios.sventon.svnsupport;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;

/**
 * Appends line numbers to strings.
 *
 * @author jesper@users.berlios.de
 */
public final class LineNumberAppender {

  private String embedStart = "";
  private String embedEnd = "";
  private int offset;

  /**
   * Constructor.
   */
  public LineNumberAppender() {
  }

  /**
   * Sets the string to insert right <u>before</u> the line number.
   *
   * @param string The string.
   */
  public void setEmbedStart(final String string) {
    embedStart = string;
  }

  /**
   * Sets the string to insert right <u>after</u> the line number.
   *
   * @param string The string.
   */
  public void setEmbedEnd(final String string) {
    embedEnd = string;
  }

  /**
   * Sets the line number offset.
   *
   * @param offset The offset, e.g. an offset of <tt>10</tt> will
   * append <tt>11</tt> to the first line.
   */
  public void setLineNumberOffset(final int offset) {
    this.offset = offset;
  }

  /**
   * Adds line numbers to previously given input string.
   *
   * @return The string with appended line numbers.
   * @throws IOException if unable to read given string.
   */
  private String addLineNumbers(final String string) throws IOException {
    BufferedReader reader = new BufferedReader(new StringReader(string));
    StringBuilder sb = new StringBuilder();
    String tempLine;
    int lineCount = 0 + offset;
    while ((tempLine = reader.readLine()) != null) {
      sb.append(embedStart);
      sb.append(++lineCount);
      sb.append(": ");
      sb.append(embedEnd);
      sb.append(tempLine);
      sb.append(System.getProperty("line.separator"));
    }
    return sb.toString().trim();
  }

  /**
   * Adds line numbers to given input string.
   *
   * @param content The content to add line number to.
   * @return The string containing appended line numbers.
   * @throws IOException if IO error occurs.
   */
  public String appendTo(final String content) throws IOException {
    return addLineNumbers(content);
  }
}
