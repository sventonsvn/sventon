package de.berlios.sventon.svnsupport;

/**
 * Diff action bean.
 *
 * @author jesper@users.berlios.de
 */
public class DiffAction {

  public static final String DELETE_ACTION = "d";
  public static final String CHANGE_ACTION = "c";
  public static final String ADD_ACTION = "a";

  /**
   * The diff action.
   * Can be {@link de.berlios.sventon.svnsupport.DiffAction.ADD_ACTION},
   * {@link de.berlios.sventon.svnsupport.DiffAction.CHANGE_ACTION} or
   * {@link de.berlios.sventon.svnsupport.DiffAction.DELETE_ACTION}.
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
