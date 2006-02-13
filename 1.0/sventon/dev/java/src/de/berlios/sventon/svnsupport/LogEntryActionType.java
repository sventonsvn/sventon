/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
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
   * The log entry type's action.
   */
  private final String action;

  /**
   * Private constructor.
   *
   * @param action The action
   */
  private LogEntryActionType(final String action) {
    this.action = action;
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return action;
  }

}
