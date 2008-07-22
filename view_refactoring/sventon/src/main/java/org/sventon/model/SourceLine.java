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
 * Source line bean. Represents one line of text including
 * the line's {@link DiffAction}.
 *
 * @author jesper@users.berlios.de
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
  private DiffAction action;

  /**
   * Constructor.
   *
   * @param rowNumber The row number, or <tt>null</tt> if unknown.
   * @param action    The <tt>DiffAction</tt> code.
   * @param line      The source line.
   */
  public SourceLine(final Integer rowNumber, final DiffAction action, final String line) {
    this.rowNumber = rowNumber;
    this.line = line;
    this.action = action;
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
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}
