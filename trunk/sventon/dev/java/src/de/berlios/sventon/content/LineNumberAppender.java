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
package de.berlios.sventon.content;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Appends line numbers to strings.
 *
 * @author jesper@users.berlios.de
 */
public final class LineNumberAppender {

  private String embedStart = "";
  private String embedEnd = "";
  private int offset;
  private int paddingLength = 0;
  private char paddingCharacter = ' ';

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
   * Sets the length, including the actual line number, used for padding.
   * Default padding length is <tt>zero</tt>.
   *
   * @param length The padding length
   */
  public void setPadding(final int length) {
    this.paddingLength = length;
  }

  /**
   * Sets the character used for line number padding.
   * Defatul character is <tt>space</tt>.
   * @param character Padding character
   */
  public void setPaddingCharacter(final char character) {
    this.paddingCharacter = character;
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
      sb.append(StringUtils.leftPad(String.valueOf(++lineCount),
          paddingLength, paddingCharacter));
      sb.append(embedEnd);
      sb.append(tempLine);
      sb.append(System.getProperty("line.separator"));
    }
    return StringUtils.chomp(sb.toString());
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
