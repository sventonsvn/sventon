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
package de.berlios.sventon.diff;

/**
 * Type enum for diff segment actions.
 *
 * @author jesper@users.berlios.de
 */
public enum DiffAction {
  DELETED("Deleted", 'd', "-"),
  CHANGED("Changed", 'c', "&#8800;"),
  ADDED("Added", 'a', "+"),
  UNCHANGED("Unchanged", 'u', "&nbsp;");

  /**
   * The diff segment action description.
   */
  private final String description;

  /**
   * The diff segment action symbol.
   */
  private final String symbol;

  /**
   * Action code.
   */
  private final char code;

  /**
   * Private constructor.
   *
   * @param description The description
   * @param code        The code
   * @param symbol      The diff symbol
   */
  private DiffAction(final String description, final char code, final String symbol) {
    this.description = description;
    this.code = code;
    this.symbol = symbol;
  }

  /**
   * Parses given code and returns apropriate <code>DiffAction</code>.
   *
   * @param code Code to parse
   * @return The DiffAction
   * @throws IllegalArgumentException if code was null
   */
  public static DiffAction parse(final String code) {
    if (code == null) {
      throw new IllegalArgumentException("Unable to parse code as it was null");
    }
    return parse(code.charAt(0));
  }

  /**
   * Parses given code and returns apropriate <code>DiffAction</code>.
   *
   * @param code Code to parse
   * @return The DiffAction
   * @throws IllegalArgumentException if unable to parse code.
   */
  public static DiffAction parse(final char code) {
    switch (code) {
      case'd':
        return DELETED;
      case'c':
        return CHANGED;
      case'a':
        return ADDED;
      case'u':
        return UNCHANGED;
      default:
        throw new IllegalArgumentException("Unable to parse code: " + code);
    }
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
   * Gets the code.
   *
   * @return The code
   */
  public char getCode() {
    return code;
  }

}
