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