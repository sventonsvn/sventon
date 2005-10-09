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
  public DiffAction(final String action,
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
  public int getLeftLineIntervalStart() {
    return this.leftLineIntervalStart;
  }

  /**
   * Gets diff interval end line.
   *
   * @return The end line
   */
  public int getLeftLineIntervalEnd() {
    return this.leftLineIntervalEnd;
  }

  /**
   * Gets diff interval start line.
   *
   * @return The start line
   */
  public int getRightLineIntervalStart() {
    return this.rightLineIntervalStart;
  }

  /**
   * Gets diff interval end line.
   *
   * @return The end line
   */
  public int getRightLineIntervalEnd() {
    return this.rightLineIntervalEnd;
  }
  /**
   * {@inheritDoc}
   */
  public String toString() {
    return "DiffAction: " + action
        + ", left: " + leftLineIntervalStart + "-" + leftLineIntervalEnd
        + ", right: " + rightLineIntervalStart + "-" + rightLineIntervalEnd;
  }
}
