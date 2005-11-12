package de.berlios.sventon.svnsupport;

/**
 * Type enum for log entry actions.
 *
 * @author jesper@users.berlios.de
 */
public enum LogEntryActionType {
  A("Added"),
  D("Deleted"),
  M("Modified"),
  R("Replaced");

  /**
   * The log entry type's description.
   */
  private final String description;

  /**
   * Private constructor.
   *
   * @param description The description
   */
  private LogEntryActionType(final String description) {
    this.description = description;
  }

  /**
   * Gets the type's description.
   *
   * @return The description
   */
  public String getDescription() {
    return description;
  }

}
