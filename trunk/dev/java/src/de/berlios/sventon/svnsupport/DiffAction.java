package de.berlios.sventon.svnsupport;

/**
 * Diff action bean.
 *
 * @author jesper@users.berlios.de
 */
public class DiffAction {

  /**
   * The diff action.
   * Can be {@link de.berlios.sventon.svnsupport.DiffHelper.ADD_ACTION},
   * {@link de.berlios.sventon.svnsupport.DiffHelper.CHANGE_ACTION} or
   * {@link de.berlios.sventon.svnsupport.DiffHelper.DELETE_ACTION}.
   */
  private String action;

  /**
   * Diff interval line start.
   */
  private int lineIntervalStart;

  /**
   * Diff interval line end.
   */
  private int lineIntervalEnd;

  /**
   * Constructor.
   *
   * @param action The diff action
   * @param lineIntervalStart Diff interval start line
   * @param lineIntervalEnd Diff interval end line
   */
  public DiffAction(final String action, final int lineIntervalStart, final int lineIntervalEnd) {
    this.action = action;
    this.lineIntervalStart = lineIntervalStart;
    this.lineIntervalEnd = lineIntervalEnd;
  }

  /**
   * Gets the action.
   *
   * @return The action
   */
  public String getAction() {
    return this.action;
  }

  /**
   * Gets diff interval start line.
   *
   * @return The start line
   */
  public int getLineIntervalStart() {
    return this.lineIntervalStart;
  }

  /**
   * Gets diff interval end line.
   *
   * @return The end line
   */
  public int getLineIntervalEnd() {
    return this.lineIntervalEnd;
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return "DiffAction: " + action + "," + lineIntervalStart + "-" + lineIntervalEnd;
  }
}
