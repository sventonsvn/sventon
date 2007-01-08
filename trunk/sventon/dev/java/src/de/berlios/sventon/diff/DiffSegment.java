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
package de.berlios.sventon.diff;

import java.util.Map;
import java.util.HashMap;

/**
 * Diff segment bean. Represents one result segment produced by
 * {@link de.berlios.sventon.diff.DiffProducer#doNormalDiff(java.io.OutputStream)}.
 *
 * @author jesper@users.berlios.de
 */
public class DiffSegment {

  public enum Side {
    LEFT, RIGHT;

    public Side opposite() {
      return this == LEFT ? RIGHT : LEFT;
    }
  }

  private DiffAction action;

  private Map<Side, Interval> segmentSides = new HashMap<Side, Interval>(2);


  /**
   * Constructor.
   *
   * @param action                 The diff action
   * @param leftLineIntervalStart  Left interval start line
   * @param leftLineIntervalEnd    Left interval end line
   * @param rightLineIntervalStart Right interval start line
   * @param rightLineIntervalEnd   Right interval end line
   */
  public DiffSegment(final DiffAction action,
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
  public DiffAction getAction() {
    return action;
  }

  public int getLineIntervalStart(final Side side) {
    return segmentSides.get(side).getStart();
  }

  public int getLineIntervalEnd(final Side side) {
    return segmentSides.get(side).getEnd();
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return "DiffSegment: " + action
        + ", left: " + segmentSides.get(Side.LEFT)
        + ", right: " + segmentSides.get(Side.RIGHT);
  }
}
