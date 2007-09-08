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

import de.berlios.sventon.content.KeywordHandler;

import java.io.IOException;
import java.util.Map;

/**
 * Represents a text file with expanded keywords.
 *
 * @author jesper@users.berlios.de
 */
public class TextFile extends RawTextFile {

  /**
   * Constructor.
   *
   * @param content       The file content.
   * @param properties    The file properties
   * @param repositoryURL The repository root URL
   * @param path          The target path
   * @param encoding      Encoding
   */
  public TextFile(final String content, final Map properties, final String repositoryURL, final String path,
                  final String encoding)
      throws IOException {

    super(content);
    final KeywordHandler keywordHandler = new KeywordHandler(properties, repositoryURL + path);
    this.content = keywordHandler.substitute(content, encoding);

    model.put("fileContent", this.content);
    model.put("isRawFormat", false);
  }

}
