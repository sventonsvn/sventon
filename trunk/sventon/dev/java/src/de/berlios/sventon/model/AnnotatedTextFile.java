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
package de.berlios.sventon.model;

import de.berlios.sventon.colorer.Colorer;
import de.berlios.sventon.content.KeywordHandler;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.util.*;

/**
 * Represents an annotated (blamed) file.
 *
 * @author jesper@users.berlios.de
 */
public class AnnotatedTextFile {

  /**
   * The annotated rows.
   */
  private final List<AnnotatedTextFileRow> rows = new ArrayList<AnnotatedTextFileRow>();

  private static final String NEWLINE = System.getProperty("line.separator");
  private String path;
  private String encoding;
  private Colorer colorer;
  private Map properties;
  private String repositoryURL;

  /**
   * Constructor.
   */
  public AnnotatedTextFile() {
    this(null, null, new Colorer() {
      public String getColorizedContent(final String content, final String fileExtension, final String encoding) {
        return content;
      }
    }, null, null);
  }

  /**
   * Constructor.
   *
   * @param path          Path.
   * @param encoding      Encoding.
   * @param colorer       Colorer.
   * @param properties    Keywords to be substituted. If <tt>null</tt> no keywords will be processed.
   * @param repositoryURL Respository URL for keyword substitution.
   */
  public AnnotatedTextFile(final String path, final String encoding, final Colorer colorer, final Map properties,
                           final String repositoryURL) {
    this.path = path;
    this.encoding = encoding;
    this.colorer = colorer;
    this.properties = properties;
    this.repositoryURL = repositoryURL;
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
      sb.append(row.getContent()).append(NEWLINE);
    }

    final String processedContent;
    if (properties != null) {
      final KeywordHandler keywordHandler = new KeywordHandler(properties, repositoryURL + path);
      processedContent = keywordHandler.substitute(sb.toString(), encoding);
    } else {
      processedContent = sb.toString();
    }

    final String colorizedContent = colorer.getColorizedContent(
        processedContent, FilenameUtils.getExtension(path), encoding);

    final String[] fileRows = colorizedContent.split(TextFile.NL_REGEXP);
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
      sb.append(row.getContent()).append(NEWLINE);
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

}
