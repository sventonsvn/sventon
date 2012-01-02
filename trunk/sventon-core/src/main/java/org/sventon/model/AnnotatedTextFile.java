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
package org.sventon.model;

import org.apache.commons.io.FilenameUtils;
import org.sventon.Colorer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents an annotated (blamed) file.
 *
 * @author jesper@sventon.org
 */
public final class AnnotatedTextFile {

  /**
   * The annotated rows.
   */
  private final List<AnnotatedTextFileRow> rows = new ArrayList<AnnotatedTextFileRow>();

  private static final String NL = System.getProperty("line.separator");

  public static final Pattern NL_REGEXP = Pattern.compile("(\r\n|\r|\n|\n\r)");

  private final String path;
  private final String encoding;
  private final Colorer colorer;

  /**
   * Constructor.
   */
  public AnnotatedTextFile() {
    this(null, null, new Colorer() {
      public String getColorizedContent(final String content, final String fileExtension, final String encoding) {
        return content;
      }
    });
  }

  /**
   * Constructor.
   *
   * @param path     Path.
   * @param encoding Encoding.
   * @param colorer  Colorer.
   */
  public AnnotatedTextFile(final String path, final String encoding, final Colorer colorer) {
    this.path = path;
    this.encoding = encoding;
    this.colorer = colorer;
  }

  /**
   * @param date     Date
   * @param revision Revision
   * @param author   Author
   * @param content  Row content
   */
  public void addRow(final Date date, final long revision, final String author, final String content) {
    rows.add(new AnnotatedTextFileRow(date, revision, author, rows.size() + 1, content));
  }

  /**
   * Colorizes the file.
   *
   * @throws IOException if unable to colorize file.
   */
  public void colorize() throws IOException {
    if (colorer == null) {
      throw new IllegalStateException("No Colorer has been set");
    }

    final StringBuilder sb = new StringBuilder();
    for (final AnnotatedTextFileRow row : rows) {
      sb.append(row.getContent()).append(NL);
    }

    final String colorizedContent = colorer.getColorizedContent(
        sb.toString(), FilenameUtils.getExtension(path), encoding);

    final String[] fileRows = NL_REGEXP.split(colorizedContent);
    int count = 0;
    for (final String fileRow : fileRows) {
      rows.get(count++).setContent(fileRow);
    }
  }

  /**
   * Gets the file content.
   *
   * @return Content
   */
  public String getContent() {
    final StringBuilder sb = new StringBuilder();
    for (final AnnotatedTextFileRow row : rows) {
      sb.append(row.getContent()).append(NL);
    }
    return sb.toString();
  }

  /**
   * Gets the unmodifiable rows.
   *
   * @return Rows
   */
  public List<AnnotatedTextFileRow> getUnmodifiableRows() {
    return Collections.unmodifiableList(rows);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for (AnnotatedTextFileRow row : rows) {
      sb.append(row.getRevision());
      sb.append("|");
      sb.append(row.getAuthor());
      sb.append("|");
      sb.append(row.getRowNumber());
      sb.append("|");
      sb.append(row.getDate());
      sb.append("|");
      sb.append(row.getContent());
      sb.append("\n");
    }
    return sb.toString();
  }
}
