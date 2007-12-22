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

import de.berlios.sventon.diff.SourceLine;

/**
 * Represents a row in a side-by-side diff view.
 *
 * @author jesper@users.berlios.de
 */
public final class SideBySideDiffRow {

  /**
   * The left source line.
   */
  private final SourceLine left;

  /**
   * The right source line.
   */
  private final SourceLine right;

  /**
   * Represents the sides.
   */
  public enum Side {
    LEFT, RIGHT
  }

  /**
   * Constructor.
   *
   * @param left      Left source line
   * @param right     Right source line
   */
  public SideBySideDiffRow(final SourceLine left, final SourceLine right) {
    this.left = left;
    this.right = right;
  }

  /**
   * Gets either of the the row's sides.
   *
   * @param side Side to get
   * @return The row side.
   */
  public SourceLine getSide(final Side side) {
    return (side == Side.LEFT ? left : right);
  }

  /**
   * Checks if row is marked as unchanged.
   *
   * @return <tt>true</tt> if row is unchanged.
   */
  public boolean getIsUnchanged() {
    return 'u' == left.getAction().getCode();
  }

}
