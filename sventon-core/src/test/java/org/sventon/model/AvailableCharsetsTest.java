/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class AvailableCharsetsTest {

  @Test
  public void testGetCharsets() throws Exception {
    final AvailableCharsets availableCharsets = new AvailableCharsets("UTF-8");
    assertFalse(availableCharsets.getCharsets().isEmpty());
  }

  @Test
  public void testGetDefaultCharset() throws Exception {
    try {
      new AvailableCharsets("abc");
      fail("Illegal charset");
    } catch (Exception e) {
      // expected
    }
  }
}