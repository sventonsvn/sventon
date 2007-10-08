package de.berlios.sventon.diff;

/**
 * Interval.
 *
 * @author jesper@users.berlios.de
 */
public final class Interval {

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
  public Interval(final int start, final int end) {
    this.start = start;
    this.end = end;
  }

  /**
   * Gets the start position.
   *
   * @return Start position.
   */
  public int getStart() {
    return start;
  }

  /**
   * Gets the end position.
   *
   * @return End position.
   */
  public int getEnd() {
    return end;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return start + "-" + end;
  }

}
