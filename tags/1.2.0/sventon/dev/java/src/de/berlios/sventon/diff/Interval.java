package de.berlios.sventon.diff;

/**
 * Interval.
 *
 * @author jesper@users.berlios.de
 */
public class Interval {

  private final int start;
  private final int end;

  public Interval(final int start, final int end) {
    this.start = start;
    this.end = end;
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return end;
  }

  public String toString() {
    return start + "-" + end;
  }

}
