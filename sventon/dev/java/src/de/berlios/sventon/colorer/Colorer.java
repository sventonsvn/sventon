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
package de.berlios.sventon.colorer;

/**
 * Colorer interface.
 *
 * @author jesper@users.berlios.de
 */
public interface Colorer {

  /**
   * Converts given contents into colorized HTML code.
   *
   * @param content       The contents.
   * @param fileExtension The filename, used to determine formatter.
   * @return The HTML formatted, colorized, string. If no suitable
   *         formatter was found for given file extension the content will still
   *         be formatted with HTML entities to be properly displayed on the web.
   */
  String getColorizedContent(final String content, final String fileExtension);
}
