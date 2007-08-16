package de.berlios.sventon.util;

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
    assertEquals("&nbsp;&nbsp;class {  ", WebUtils.replaceLeadingSpaces("  class {  "));
  }

}
