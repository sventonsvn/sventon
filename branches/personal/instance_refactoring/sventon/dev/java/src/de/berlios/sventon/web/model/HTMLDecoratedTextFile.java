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
package de.berlios.sventon.web.model;

import de.berlios.sventon.colorer.Colorer;
import de.berlios.sventon.content.LineNumberAppender;
import de.berlios.sventon.util.PathUtil;

import java.io.IOException;
import java.util.Map;

/**
 * Represents a HTML decorated text file.
 * Keywords will be expanded and the file will be colorized depending on it's format.
 *
 * @author jesper@users.berlios.de
 */
public class HTMLDecoratedTextFile extends TextFile {

  /**
   * Constructor.
   *
   * @param content       The file content.
   * @param properties    The file properties
   * @param repositoryURL The repository root URL
   * @param path          The target path
   * @param encoding      Encoding
   * @param colorer       The colorer instance
   */
  public HTMLDecoratedTextFile(final String content, final Map properties, final String repositoryURL,
                               final String path, final String encoding, final Colorer colorer) throws IOException {

    super(content, properties, repositoryURL, path, encoding);

    final LineNumberAppender appender = new LineNumberAppender();
    appender.setEmbedStart("<span class=\"sventonLineNo\">");
    appender.setEmbedEnd(":&nbsp;</span>");
    appender.setPadding(5);
    this.content = appender.appendTo(colorer.getColorizedContent(this.content, PathUtil.getFileExtension(
        PathUtil.getTarget(path)), encoding));

    model.put("fileContent", this.content);
    model.put("isRawFormat", false);
  }

}
