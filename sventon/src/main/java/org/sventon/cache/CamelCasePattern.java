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
package org.sventon.cache;

import org.apache.commons.lang.Validate;

/**
 * CamelCase regex pattern class.
 *
 * @author jesper@users.berlios.de
 */
public final class CamelCasePattern {

  /**
   * The pattern string.
   */
  private final String camelCasePattern;

  /**
   * Constructor.
   * Creates a regex pattern based on given CamelCase string.
   *
   * @param camelCaseString The CamelCase string
   */
  public CamelCasePattern(final String camelCaseString) {
    Validate.notEmpty(camelCaseString, "Illegal camelCase string: " + camelCaseString);

    final StringBuilder pattern = new StringBuilder();
    for (int i = 0; i < camelCaseString.length(); i++) {
      pattern.append("[");
      pattern.append(camelCaseString.charAt(i));
      pattern.append("][a-z0-9]*?");
    }
    pattern.append(".*");
    camelCasePattern = pattern.toString();
  }

  /**
   * Gets the regex pattern for the CamelCase string.
   *
   * @return The regex pattern.
   */
  public String getPattern() {
    return camelCasePattern;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return camelCasePattern;
  }

}
