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
 * Source line bean.
 *
 * @author jesper@users.berlios.de
 */
public class SourceLine {

  private DiffAction action;
  private String line;

  /**
   * Constructor.
   *
   * @param action The <tt>DiffAction</tt> code.
   * @param line   The source line.
   */
  public SourceLine(final DiffAction action, final String line) {
    this.action = action;
    this.line = line;
  }

  /**
   * Gets the action code.
   *
   * @return Code
   */
  public DiffAction getAction() {
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
