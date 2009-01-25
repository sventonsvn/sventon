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

/**
 * Represents a row in a side-by-side diff view.
 *
 * @author jesper@sventon.org
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
   * Constructor.
   *
   * @param left  Left source line
   * @param right Right source line
   */
  public SideBySideDiffRow(final SourceLine left, final SourceLine right) {
    this.left = left;
    this.right = right;
  }

  /**
   * Gets the the left row.
   *
   * @return The left row side.
   */
  public SourceLine getLeft() {
    return left;
  }

  /**
   * Gets the the right row.
   *
   * @return The right row side.
   */
  public SourceLine getRight() {
    return right;
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
