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

import de.berlios.sventon.diff.SourceLine;

/**
 * Created by IntelliJ IDEA.
 *
 * @author jesper@users.berlios.de
 */
public class SideBySideDiffRow {

  private final int rowNumber;
  private final SourceLine left;
  private final SourceLine right;

  public enum Side {
    LEFT, RIGHT
  }

  /**
   * Constructor.
   *
   * @param rowNumber Row number
   * @param left      Left source line
   * @param right     Right source line
   */
  public SideBySideDiffRow(final int rowNumber, final SourceLine left, final SourceLine right) {
    this.rowNumber = rowNumber;
    this.left = left;
    this.right = right;
  }

  public int getRowNumber() {
    return rowNumber;
  }

  public SourceLine getSide(final Side side) {
    return (side == Side.LEFT ? left : right);
  }

  public boolean getIsUnchanged() {
    return 'u' == left.getAction().getCode();
  }
}
