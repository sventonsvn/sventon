/*
 * ====================================================================
 * Copyright (c) 2005 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
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
