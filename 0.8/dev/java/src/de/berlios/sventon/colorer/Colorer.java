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
package de.berlios.sventon.colorer;

/**
 * Colorer interface.
 *
 * @author jesper@users.berlios.de
 */
public interface Colorer {

  /**
   * Converts given contents into colorized HTML code.
   * @param content The contents.
   * @param filename The filename, used to determine formatter.
   * @return The colorized string.
   */
  String getColorizedContent(final String content, final String filename);
  //TODO: Method should not take filename as a parameter.
  // Better use another type of identifier instead.
}
