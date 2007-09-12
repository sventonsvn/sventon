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
package de.berlios.sventon.diff;

/**
 * Source line bean. Represents one line of text including
 * the line's {@link de.berlios.sventon.diff.DiffAction}.
 *
 * @author jesper@users.berlios.de
 */
public final class SourceLine {

  /**
   * The line's diff action.
   */
  private DiffAction action;

  /**
   * The actual source line.
   */
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
   * @return The line's {@link de.berlios.sventon.diff.DiffAction}
   */
  public DiffAction getAction() {
    return action;
  }

  /**
   * Gets the source line.
   *
   * @return The line of text.
   */
  public String getLine() {
    return line;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "SourceLine{action='" + action + "', " + "line='" + line + "'}";
  }

}
