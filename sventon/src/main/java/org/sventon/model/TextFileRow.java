/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Represents a row in a text file.
 *
 * @author jesper@users.berlios.de
 */
public final class TextFileRow {

  /**
   * Line content.
   */
  private final String content;

  /**
   * Row number.
   */
  private final int rowNumber;

  /**
   * Constructor.
   *
   * @param rowNumber Row number. Should start from 1.
   * @param content   Row content
   */
  public TextFileRow(final int rowNumber, final String content) {
    this.content = content;
    this.rowNumber = rowNumber;
  }

  /**
   * Gets the row content.
   *
   * @return Content
   */
  public String getContent() {
    return content;
  }

  /**
   * Gets the row number of this file row.
   *
   * @return Row number
   */
  public int getRowNumber() {
    return rowNumber;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}
