package de.berlios.sventon.web.model;

import junit.framework.TestCase;

public class AvailableCharsetsTest extends TestCase {

  public void testGetCharsets() throws Exception {
    final AvailableCharsets availableCharsets = new AvailableCharsets();
    assertFalse(availableCharsets.getCharsets().isEmpty());
  }
}