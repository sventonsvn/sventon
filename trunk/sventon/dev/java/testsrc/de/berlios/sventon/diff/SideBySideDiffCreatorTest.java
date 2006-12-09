package de.berlios.sventon.diff;

import junit.framework.TestCase;

public class SideBySideDiffCreatorTest extends TestCase {

  private static final String BR = System.getProperty("line.separator");

  public void testReplaceLeadingSpaces() throws Exception {
    final String line = " one " +
        BR + "  two  " +
        BR + "          " +
        BR + " three  four";
    final String result = SideBySideDiffCreator.replaceLeadingSpaces(line);

    final String expected = "&nbsp;one " +
        BR + "&nbsp;&nbsp;two  " +
        BR + "          " +
        BR + "&nbsp;three  four";
    assertEquals(expected, result.trim());
  }

}