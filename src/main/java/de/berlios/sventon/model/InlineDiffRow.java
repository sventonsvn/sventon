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
package de.berlios.sventon.model;

import de.berlios.sventon.diff.DiffAction;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Represents a row in an inline diff view.
 *
 * @author jesper@users.berlios.de
 */
public final class InlineDiffRow {

  /**
   * The actual source line.
   */
  private final String line;

  /**
   * Left row number.
   */
  private final Integer rowNumberLeft;

  /**
   * Right row number.
   */
  private final Integer rowNumberRight;

  /**
   * The line's diff action.
   */
  private DiffAction action;

  /**
   * Constructor.
   *
   * @param leftRowNumber  The left (from) row number, or <tt>null</tt> if unknown.
   * @param rightRowNumber The right (to) row number, or <tt>null</tt> if unknown.
   * @param action         The <tt>DiffAction</tt> code.
   * @param line           The source line.
   */
  public InlineDiffRow(final Integer leftRowNumber, final Integer rightRowNumber, final DiffAction action, final String line) {
    this.rowNumberLeft = leftRowNumber;
    this.rowNumberRight = rightRowNumber;
    this.line = line;
    this.action = action;
  }

  /**
   * Gets the action code.
   *
   * @return The line's {@link de.berlios.sventon.diff.DiffAction}
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
   * Gets the left (from) row number.
   *
   * @return Row number or <tt>null</tt> if unknown.
   */
  public Integer getRowNumberLeft() {
    return rowNumberLeft;
  }

  /**
   * Gets the right (to) row number.
   *
   * @return Row number or <tt>null</tt> if unknown.
   */
  public Integer getRowNumberRight() {
    return rowNumberRight;
  }

  /**
   * Checks if row is marked as unchanged.
   *
   * @return <tt>true</tt> if row is unchanged.
   */
  public boolean getIsUnchanged() {
    return 'u' == action.getCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
