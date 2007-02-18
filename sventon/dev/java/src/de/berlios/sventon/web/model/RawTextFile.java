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

/**
 * Represents a text file in a raw, unprocessed, format.
 *
 * @author jesper@users.berlios.de
 */
public class RawTextFile extends AbstractFile {

  /**
   * Constructor.
   * Equivalent to {@link #RawTextFile(String, false)}.
   *
   * @param content The file content.
   */
  public RawTextFile(final String content) {
    this(content, false);
  }

  /**
   * Constructor.
   *
   * @param content    The file content.
   * @param escapeHtml If <code>true</code>, characters will be converted into
   *                   web safe characters using {@link org.apache.commons.lang.StringEscapeUtils#escapeHtml}.
   */
  public RawTextFile(final String content, final boolean escapeHtml) {
    if (escapeHtml) {
      this.content = StringEscapeUtils.escapeHtml(content);
    } else {
      this.content = content;
    }
    model.put("fileContent", this.content);
    model.put("isRawFormat", true);
  }

}
