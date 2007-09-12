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
package de.berlios.sventon.content;

import org.apache.commons.lang.StringUtils;

import java.util.Scanner;

/**
 * Appends line numbers to strings.
 * The string will be read line by line and line numbers will be appended to the
 * beginning of each row.
 *
 * @author jesper@users.berlios.de
 */
public final class LineNumberAppender {

  private String embedStart = "";
  private String embedEnd = "";
  private int offset = 0;
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
   *               append <tt>11</tt> to the first line.
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
   *
   * @param character Padding character
   */
  public void setPaddingCharacter(final char character) {
    this.paddingCharacter = character;
  }

  /**
   * Adds line numbers to given input string.
   *
   * @param content The content to add line number to.
   * @return The string containing appended line numbers.
   */
  public String appendTo(final String content) {
    final StringBuilder sb = new StringBuilder();
    final Scanner scanner = new Scanner(content);

    try {
      int lineCount = offset;
      while (scanner.hasNextLine()) {
        sb.append(embedStart);
        sb.append(StringUtils.leftPad(String.valueOf(++lineCount), paddingLength, paddingCharacter));
        sb.append(embedEnd);
        sb.append(scanner.nextLine());
        sb.append(System.getProperty("line.separator"));
      }
    } finally {
      scanner.close();
    }
    return StringUtils.chomp(sb.toString());
  }
}
