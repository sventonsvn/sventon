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
package de.berlios.sventon.web.model;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.IOException;

/**
 * Handles text files in a raw, unprocessed, format.
 * The characters have to be converted into web safe characters using
 * {@link org.apache.commons.lang.StringEscapeUtils} <code>escapeHtml</code>.
 *
 * @author jesper@users.berlios.de
 */
public class RawTextFile extends AbstractFile {

  /**
   * Constructor.
   *
   * @param contents The file contents.
   */
  public RawTextFile(final String contents) throws IOException {
    model.put("fileContents", StringEscapeUtils.escapeHtml(contents));
    model.put("isRawFormat", true);
  }

}
