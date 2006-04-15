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
