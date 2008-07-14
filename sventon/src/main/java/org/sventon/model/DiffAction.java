/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
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
 * Type enum for diff segment actions.
 *
 * @author jesper@users.berlios.de
 */
public enum DiffAction {
  DELETED("Deleted", 'd', "-", "srcDel"),
  CHANGED("Changed", 'c', "&#8800;", "srcChg"),
  ADDED("Added", 'a', "+", "srcAdd"),
  UNCHANGED("Unchanged", 'u', "&nbsp;", "src");

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
   * Action code.
   */
  private final char code;

  /**
   * Private constructor.
   *
   * @param description The description
   * @param code        Code
   * @param symbol      Symbol
   * @param cssClass    CSS style class
   */
  private DiffAction(final String description, final char code, final String symbol, final String cssClass) {
    this.description = description;
    this.code = code;
    this.symbol = symbol;
    this.cssClass = cssClass;
  }

  /**
   * Parses given code and returns apropriate <code>DiffAction</code>.
   *
   * @param code Code to parse
   * @return The DiffAction
   */
  public static DiffAction parse(final String code) {
    Validate.notEmpty(code, "Given code was null or empty");
    return parse(code.charAt(0));
  }

  /**
   * Parses given code and returns apropriate <code>DiffAction</code>.
   *
   * @param code Code to parse
   * @return The DiffAction
   */
  public static DiffAction parse(final char code) {
    switch (code) {
      case 'd':
        return DELETED;
      case 'c':
        return CHANGED;
      case 'a':
        return ADDED;
      case 'u':
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
   * Gets the associated CSS class name.
   *
   * @return The CSS class name.
   */
  public String getCSSClass() {
    return cssClass;
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
