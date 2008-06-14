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
package de.berlios.sventon.model;

import de.berlios.sventon.colorer.Colorer;
import de.berlios.sventon.content.KeywordHandler;
import de.berlios.sventon.util.WebUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents a file in plain text format.
 *
 * @author jesper@users.berlios.de
 */
public final class TextFile {

  /**
   * The text file rows.
   */
  private final List<TextFileRow> rows = new ArrayList<TextFileRow>();

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
    }, null, null);
  }

  /**
   * Constructor.
   *
   * @param content       Content.
   * @param path          Path.
   * @param encoding      Encoding.
   * @param colorer       Colorer.
   * @param properties    Keywords to be substituted. If <tt>null</tt> no keywords will be processed.
   * @param repositoryURL Respository URL for keyword substitution.
   * @throws IOException if unable to read content.
   */
  public TextFile(final String content, final String path, final String encoding, final Colorer colorer,
                  final Map properties, final String repositoryURL) throws IOException {

    String processedContent;

    if (properties != null) {
      final KeywordHandler keywordHandler = new KeywordHandler(properties, repositoryURL + path);
      processedContent = keywordHandler.substitute(content, encoding);
    } else {
      processedContent = content;
    }

    if (colorer != null) {
      processedContent = colorer.getColorizedContent(processedContent, FilenameUtils.getExtension(path), encoding);
    }
    final String[] fileRows = WebUtils.NL_REGEXP.split(processedContent);
    int count = 0;
    for (final String fileRow : fileRows) {
      rows.add(new TextFileRow(++count, WebUtils.replaceLeadingSpaces(fileRow)));
    }
  }

  /**
   * Gets the file's content.
   *
   * @return Content
   */
  public String getContent() {
    final StringBuilder sb = new StringBuilder();
    for (final TextFileRow row : rows) {
      sb.append(row.getContent()).append(System.getProperty("line.separator"));
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
