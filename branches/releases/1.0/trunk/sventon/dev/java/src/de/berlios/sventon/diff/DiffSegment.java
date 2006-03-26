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
package de.berlios.sventon.diff;

/**
 * Diff segment bean.
 *
 * @author jesper@users.berlios.de
 */
public class DiffSegment {

  private DiffAction action;

  /**
   * Left interval line start.
   */
  private int leftLineIntervalStart;

  /**
   * Left interval line end.
   */
  private int leftLineIntervalEnd;

  /**
   * Right interval line start.
   */
  private int rightLineIntervalStart;

  /**
   * Right interval line end.
   */
  private int rightLineIntervalEnd;
  
  
  /**
   * Constructor.
   *
   * @param action The diff action
   * @param leftLineIntervalStart Left interval start line
   * @param leftLineIntervalEnd Left interval end line
   * @param rightLineIntervalStart Right interval start line
   * @param rightLineIntervalEnd Right interval end line
   */
  public DiffSegment(final DiffAction action,
                     final int leftLineIntervalStart,
                     final int leftLineIntervalEnd,
                     final int rightLineIntervalStart,
                     final int rightLineIntervalEnd) {

    this.action = action;
    this.leftLineIntervalStart = leftLineIntervalStart;
    this.leftLineIntervalEnd = leftLineIntervalEnd;
    this.rightLineIntervalStart = rightLineIntervalStart;
    this.rightLineIntervalEnd = rightLineIntervalEnd;
  }

  /**
   * Gets the diff action.
   *
   * @return The diff action
   */
  public DiffAction getAction() {
    return action;
  }

  /**
   * Gets diff interval start line.
   *
   * @return The start line
   */
  public int getLeftLineIntervalStart() {
    return leftLineIntervalStart;
  }

  /**
   * Gets diff interval end line.
   *
   * @return The end line
   */
  public int getLeftLineIntervalEnd() {
    return leftLineIntervalEnd;
  }

  /**
   * Gets diff interval start line.
   *
   * @return The start line
   */
  public int getRightLineIntervalStart() {
    return rightLineIntervalStart;
  }

  /**
   * Gets diff interval end line.
   *
   * @return The end line
   */
  public int getRightLineIntervalEnd() {
    return rightLineIntervalEnd;
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return "DiffSegment: " + action
        + ", left: " + leftLineIntervalStart + "-" + leftLineIntervalEnd
        + ", right: " + rightLineIntervalStart + "-" + rightLineIntervalEnd;
  }
}
