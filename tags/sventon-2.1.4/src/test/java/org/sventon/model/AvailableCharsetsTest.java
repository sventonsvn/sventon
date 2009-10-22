package org.sventon.model;

import junit.framework.TestCase;

public class AvailableCharsetsTest extends TestCase {

  public void testGetCharsets() throws Exception {
    final AvailableCharsets availableCharsets = new AvailableCharsets("UTF-8");
    assertFalse(availableCharsets.getCharsets().isEmpty());
  }

  public void testGetDefaultCharset() throws Exception {
    try {
      new AvailableCharsets("abc");
      fail("Illegal charset");
    } catch (Exception e) {
      // expected
    }
  }
}