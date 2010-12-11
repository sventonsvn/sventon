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

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * CamelCasePattern.
 *
 * @author jesper@sventon.org
 */
public final class CamelCasePattern implements Serializable {

  private static final long serialVersionUID = -6140176115713331470L;

  /**
   * Pattern.
   */
  private final String pattern;

  /**
   * Constructor.
   *
   * @param pattern Pattern.
   */
  public CamelCasePattern(final String pattern) {
    if (isAllUpperCase(pattern)) {
      this.pattern = pattern;
    } else {
      throw new IllegalArgumentException("Not a valid camel case pattern");
    }
  }

  /**
   * Gets the pattern.
   *
   * @return Pattern
   */
  public String getPattern() {
    return pattern;
  }

  @Override
  public String toString() {
    return pattern;
  }

  /**
   * Validates given string and returns true if pattern is valid, i.e. not empty, length > 1 and in all upper case.
   *
   * @param pattern The pattern to validate
   * @return True if valid, false if not.
   */
  public static boolean isValid(final String pattern) {
    return isAllUpperCase(pattern);
  }


  /**
   * Checks if all characters in given string is in upper case.
   *
   * @param str String.
   * @return True if all is uppercase.
   */
  protected static boolean isAllUpperCase(final String str) {
    return StringUtils.trimToEmpty(str).toUpperCase().equals(str);
  }

  /**
   * Extracts the camel case pattern from given name.
   *
   * @param name Name
   * @return CamelCase pattern. Eg. the input <i>OneTwoThree</i> would give <i>OTT</i>.
   */
  protected static CamelCasePattern parse(final String name) {
    final StringBuilder sb = new StringBuilder();
    if (!StringUtils.isEmpty(name) && Character.isUpperCase(name.charAt(0))) {
      for (int i = 0; i < name.length(); i++) {
        final char character = name.charAt(i);
        if (Character.isUpperCase(character)) {
          sb.append(character);
        }
      }
    }
    if (sb.length() < 2) {
      throw new IllegalArgumentException("Not a valid pattern");
    } else {
      return new CamelCasePattern(sb.toString());
    }
  }

}
