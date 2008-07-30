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
package org.sventon.diff;

import org.sventon.model.DiffAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Diff segment bean. Represents one result segment produced by
 * {@link org.sventon.diff.DiffProducer#doNormalDiff(java.io.OutputStream)}.
 *
 * @author jesper@sventon.org
 */
final class DiffSegment {

  /**
   * Represents the two sides.
   */
  enum Side {

    /**
     * Left side.
     */
    LEFT,

    /**
     * Right side.
     */
    RIGHT;

    /**
     * Gets the opposite side.
     *
     * @return The opposite side.
     */
    Side opposite() {
      return this == LEFT ? RIGHT : LEFT;
    }
  }

  /**
   * The diff action.
   */
  private final DiffAction action;

  private final Map<Side, Interval> segmentSides = new HashMap<Side, Interval>(2);


  /**
   * Constructor.
   *
   * @param action                 The diff action
   * @param leftLineIntervalStart  Left interval start line
   * @param leftLineIntervalEnd    Left interval end line
   * @param rightLineIntervalStart Right interval start line
   * @param rightLineIntervalEnd   Right interval end line
   */
  DiffSegment(final DiffAction action,
              final int leftLineIntervalStart,
              final int leftLineIntervalEnd,
              final int rightLineIntervalStart,
              final int rightLineIntervalEnd) {

    this.action = action;
    segmentSides.put(Side.LEFT, new Interval(leftLineIntervalStart, leftLineIntervalEnd));
    segmentSides.put(Side.RIGHT, new Interval(rightLineIntervalStart, rightLineIntervalEnd));
  }

  /**
   * Gets the diff action.
   *
   * @return The diff action
   */
  DiffAction getAction() {
    return action;
  }

  /**
   * Gets the line interval start.
   *
   * @param side Side
   * @return Start
   */
  int getLineIntervalStart(final Side side) {
    return segmentSides.get(side).getStart();
  }

  /**
   * Gets the line interval end.
   *
   * @param side Side
   * @return End
   */
  int getLineIntervalEnd(final Side side) {
    return segmentSides.get(side).getEnd();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "DiffSegment: " + action
        + ", left: " + segmentSides.get(Side.LEFT)
        + ", right: " + segmentSides.get(Side.RIGHT);
  }
}
