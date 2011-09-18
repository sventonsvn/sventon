/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.model;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.sventon.Colorer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents a file in plain text format.
 *
 * @author jesper@sventon.org
 */
public final class TextFile {

  /**
   * The text file rows.
   */
  private final List<TextFileRow> rows = new ArrayList<TextFileRow>();

  private static final String NL = System.getProperty("line.separator");

  public static final String NBSP = "&nbsp;";

  public static final Pattern NL_REGEXP = Pattern.compile("(\r\n|\r|\n|\n\r)");

  /**
   * Constructor.
   *
   * @param content Content
   * @throws IOException if unable to read content.
   */
  public TextFile(final String content) throws IOException {
    this(content, null, null, new Colorer() {
      public String getColorizedContent(final String content, final String fileExtension, final String encoding) {
        return StringEscapeUtils.escapeXml(content);
      }
    });
  }

  /**
   * Constructor.
   *
   * @param content  Content.
   * @param path     Path.
   * @param encoding Encoding.
   * @param colorer  Colorer.
   * @throws IOException if unable to read content.
   */
  public TextFile(final String content, final String path, final String encoding, final Colorer colorer) throws IOException {
    String processedContent = content;
    if (colorer != null) {
      processedContent = colorer.getColorizedContent(processedContent, FilenameUtils.getExtension(path), encoding);
    }
    final String[] fileRows = NL_REGEXP.split(processedContent);
    int count = 0;
    for (final String fileRow : fileRows) {
      rows.add(new TextFileRow(++count, replaceLeadingSpaces(fileRow)));
    }
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
        sb.append(NL);
      }
    }
    return sb.toString();
  }

  /**
   * Gets the file's content.
   *
   * @return Content
   */
  public String getContent() {
    final StringBuilder sb = new StringBuilder();
    for (final TextFileRow row : rows) {
      sb.append(row.getContent()).append(NL);
    }
    return sb.toString();
  }

  /**
   * Gets the unmodifiable rows.
   *
   * @return Rows
   */
  public List<TextFileRow> getRows() {
    return Collections.unmodifiableList(rows);
  }

  /**
   * Gets the number of rows (i.e. size) of this text file.
   *
   * @return Size
   */
  public int size() {
    return rows.size();
  }
}
