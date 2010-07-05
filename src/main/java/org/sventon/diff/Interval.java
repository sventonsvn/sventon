/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.diff;

/**
 * Interval.
 *
 * @author jesper@sventon.org
 */
final class Interval {

  /**
   * Start position.
   */
  private final int start;

  /**
   * End position.
   */
  private final int end;

  /**
   * Constructor.
   *
   * @param start Start position.
   * @param end   End position.
   */
  Interval(final int start, final int end) {
    this.start = start;
    this.end = end;
  }

  /**
   * Gets the start position.
   *
   * @return Start position.
   */
  int getStart() {
    return start;
  }

  /**
   * Gets the end position.
   *
   * @return End position.
   */
  int getEnd() {
    return end;
  }

  @Override
  public String toString() {
    return start + "-" + end;
  }

}
