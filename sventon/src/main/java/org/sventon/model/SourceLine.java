/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
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
 * Source line bean. Represents one line of text including
 * the line's {@link DiffAction}.
 *
 * @author jesper@sventon.org
 */
public final class SourceLine {

  /**
   * The actual source line.
   */
  private final String line;

  /**
   * Row number.
   */
  private final Integer rowNumber;

  /**
   * The line's diff action.
   */
  private DiffAction action = DiffAction.UNCHANGED;

  /**
   * Constructor.
   * Creates an empty source line with a <tt>null</tt> as line number.
   *
   * @param action The <tt>DiffAction</tt> code.
   */
  public SourceLine(final DiffAction action) {
    this.rowNumber = null;
    this.line = "";
    this.action = action;
  }

  /**
   * Constructor.
   *
   * @param rowNumber The row number, or <tt>null</tt> if unknown.
   * @param line      The source line.
   */
  public SourceLine(final Integer rowNumber, final String line) {
    this.rowNumber = rowNumber;
    this.line = line;
  }

  /**
   * Changes the line's diff action.
   *
   * @param action New diff action.
   * @return Itself
   */
  public SourceLine changeAction(final DiffAction action) {
    this.action = action;
    return this;
  }

  /**
   * Gets the action code.
   *
   * @return The line's {@link DiffAction}
   */
  public DiffAction getAction() {
    return action;
  }

  /**
   * Gets the source line.
   *
   * @return The line of text.
   */
  public String getLine() {
    return line;
  }

  /**
   * Gets the row number.
   *
   * @return Row number or <tt>null</tt> if unknown.
   */
  public Integer getRowNumber() {
    return rowNumber;
  }

  /**
   * @return Returns true if this line has a row number, false if not.
   */
  public boolean hasRowNumber() {
    return rowNumber != null;
  }

  /**
   * Gets the row number as a String.
   *
   * @return The row number as a String.
   */
  public String getRowNumberAsString() {
    if (rowNumber == null) {
      return "";
    } else {
      return rowNumber.toString();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}
