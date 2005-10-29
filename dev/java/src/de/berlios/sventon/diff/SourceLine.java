package de.berlios.sventon.diff;

/**
 * Source line bean.
 *
 * @author jesper@users.berlios.de
 */
public class SourceLine {

  private String action;
  private String line;

  /**
   * Constructor.
   *
   * @param action The <tt>action</tt> code.
   * @param line   The source line.
   */
  public SourceLine(final String action, final String line) {
    this.action = action;
    this.line = line;
  }

  /**
   * Gets the action code.
   *
   * @return Code
   */
  public String getAction() {
    return action;
  }

  /**
   * Gets the source line.
   *
   * @return Line
   */
  public String getLine() {
    return line;
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return "SourceLine{action='" + action + "', " + "line='" + line + "'}";
  }

}
