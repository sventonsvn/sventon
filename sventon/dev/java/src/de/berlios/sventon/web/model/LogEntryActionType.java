/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.model;

/**
 * Type enum for log entry actions.
 *
 * @author jesper@users.berlios.de
 */
public enum LogEntryActionType {
  ADDED("Added", 'A'),
  DELETED("Deleted", 'D'),
  MODIFIED("Modified", 'M'),
  REPLACED("Replaced", 'R');

  /**
   * The log entry type's action.
   */
  private final String action;

  /**
   * The action code.
   */
  private final char code;

  /**
   * Private constructor.
   *
   * @param action The action
   * @param code Code
   */
  private LogEntryActionType(final String action, final char code) {
    this.action = action;
    this.code = code;
  }

  /**
   * Parses given code and returns apropriate <code>LogEntryActionType</code>.
   *
   * @param code Code to parse
   * @return The LogEntryActionType
   * @throws IllegalArgumentException if code was null
   */
  public static LogEntryActionType parse(final String code) {
    if (code == null) {
      throw new IllegalArgumentException("Unable to parse code as it was null");
    }
    return parse(code.charAt(0));
  }

  /**
   * Parses given code and returns apropriate <code>LogEntryActionType</code>.
   *
   * @param code Code to parse
   * @return The LogEntryActionType
   * @throws IllegalArgumentException if unable to parse code.
   */
  public static LogEntryActionType parse(final char code) {
    switch (code) {
      case 'D':
        return DELETED;
      case 'M':
        return MODIFIED;
      case 'A':
        return ADDED;
      case 'R':
        return REPLACED;
      default:
        throw new IllegalArgumentException("Unable to parse code: " + code);
    }
  }

  /**
   * Gets the action code.
   *
   * @return The code
   */
  public char getCode() {
    return code;
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return action;
  }

}
