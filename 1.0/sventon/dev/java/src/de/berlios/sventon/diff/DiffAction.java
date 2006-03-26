package de.berlios.sventon.diff;

/**
 * Type enum for diff segment actions.
 *
 * @author jesper@users.berlios.de
 */
public enum DiffAction {
  d("Deleted", "-", "srcDel"),
  c("Changed", "&#8800;", "srcChg"),
  a("Added", "+", "srcAdd"),
  u("Unchanged", "&nbsp;", "src");

  /**
   * The diff segment action description.
   */
  private final String description;

  /**
   * The diff segment action symbol.
   */
  private final String symbol;

  /**
   * CSS (Cascading style sheet) class name.
   */
  private final String cssClass;
  /**
   * Private constructor.
   *
   * @param description The description
   */
  private DiffAction(final String description, final String symbol, final String cssClass) {
    this.description = description;
    this.symbol = symbol;
    this.cssClass = cssClass;
  }

  /**
   * Gets the action's associated symbol.
   *
   * @return The symbol.
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * Gets the description.
   *
   * @return The description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the associated CSS class name.
   *
   * @return The CSS class name.
   */
  public String getCSSClass() {
    return cssClass;
  }

}
