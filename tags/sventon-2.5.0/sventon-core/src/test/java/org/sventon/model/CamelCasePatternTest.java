/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class CamelCasePatternTest {

  @Test
  public void testExtractCamelCasePattern() {
    try {
      CamelCasePattern.parse(null).getPattern();
      fail("Should trow IAE");
    } catch (IllegalArgumentException e) {
      // expected
    }

    try {
      CamelCasePattern.parse("").getPattern();
      fail("Should trow IAE");
    } catch (IllegalArgumentException e) {
      // expected
    }

    try {
      CamelCasePattern.parse("One").getPattern();
      fail("Should trow IAE");
    } catch (IllegalArgumentException e) {
      // expected
    }

    try {
      CamelCasePattern.parse("one").getPattern();
      fail("Should trow IAE");
    } catch (IllegalArgumentException e) {
      // expected
    }

    try {
      CamelCasePattern.parse("oneTwo").getPattern();
      fail("Should trow IAE");
    } catch (IllegalArgumentException e) {
      // expected
    }

    assertEquals("OT", CamelCasePattern.parse("OneT").getPattern());
    assertEquals("OT", CamelCasePattern.parse("OneTwo").getPattern());
    assertEquals("OTT", CamelCasePattern.parse("OneTwoThree").getPattern());
  }

  @Test()
  public void testIsAllUpperCase() {
    assertFalse(CamelCasePattern.isAllUpperCase("a"));
    assertFalse(CamelCasePattern.isAllUpperCase("aa"));
    assertFalse(CamelCasePattern.isAllUpperCase("aAa"));
    assertTrue(CamelCasePattern.isAllUpperCase("A"));
    assertTrue(CamelCasePattern.isAllUpperCase("AA"));
    assertFalse(CamelCasePattern.isAllUpperCase("AaA"));
  }

}
