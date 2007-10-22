package de.berlios.sventon.util;

import static de.berlios.sventon.util.WebUtils.BR;
import junit.framework.TestCase;

public class WebUtilsTest extends TestCase {

  public void testNl2br() throws Exception {
    assertEquals("one<br/>two", WebUtils.nl2br("one\ntwo"));
  }

  public void testReplaceLeadingSpaces() throws Exception {

    try {
      WebUtils.replaceLeadingSpaces(null);
    } catch (NullPointerException npe) {
      // expected
    }
    assertEquals("", WebUtils.replaceLeadingSpaces(""));
    assertEquals("&nbsp;c", WebUtils.replaceLeadingSpaces(" c"));
    assertEquals("&nbsp;&nbsp;class {", WebUtils.replaceLeadingSpaces("  class {"));
  }

  public void testReplaceLeadingSpacesMultiline() throws Exception {
    final String line = " one " +
        BR + "  two  " +
        BR + "          " +
        BR + " three  four";
    final String result = WebUtils.replaceLeadingSpaces(line);

    final String expected = "&nbsp;one " +
        BR + "&nbsp;&nbsp;two  " +
        BR + "          " +
        BR + "&nbsp;three  four";
    assertEquals(expected, result.trim());
  }

}
