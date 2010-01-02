/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.model;

import org.apache.commons.lang.Validate;

/**
 * Type enum for log entry actions.
 *
 * @author jesper@sventon.org
 */
public enum LogEntryActionType {

  /**
   * Indicating entry was added.
   */
  ADDED("Added", 'A'),

  /**
   * Indicating entry was deleted.
   */
  DELETED("Deleted", 'D'),

  /**
   * Indicating entry was modified.
   */
  MODIFIED("Modified", 'M'),

  /**
   * Indicating entry was replaced (meaning that the object is first deleted,
   * then another object of the same name is added,
   * all within a single revision).
   */
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
   * @param code   Code
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
   */
  public static LogEntryActionType parse(final String code) {
    Validate.notEmpty(code, "Given code was null or empty");
    return parse(code.charAt(0));
  }

  /**
   * Parses given code and returns apropriate <code>LogEntryActionType</code>.
   *
   * @param code Code to parse
   * @return The LogEntryActionType
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
