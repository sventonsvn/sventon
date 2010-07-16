package org.sventon.util;

import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import static org.sventon.util.WebUtils.NL;

public class WebUtilsTest extends TestCase {

  public void testNl2br() throws Exception {
    assertEquals("one<br>two", WebUtils.nl2br("one\ntwo"));
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
    String line = " one " +
        NL + "  two  " +
        NL + "          " +
        NL + " three  four" + NL;

    String expected = "&nbsp;one " +
        NL + "&nbsp;&nbsp;two  " +
        NL + "          " +
        NL + "&nbsp;three  four" + NL;

    assertEquals(expected, WebUtils.replaceLeadingSpaces(line));

    line = NL + " one " +
        NL + "  two  ";

    expected = NL + "&nbsp;one " +
        NL + "&nbsp;&nbsp;two  " + NL;

    assertEquals(expected, WebUtils.replaceLeadingSpaces(line));
  }

  public void testExtractBaseURLFromRequest() throws Exception {
    MockHttpServletRequest request;

    request = new MockHttpServletRequest("GET", "foo/bar");
    request.setServerName("www.test.com");
    request.setServerPort(80);
    assertEquals("http://www.test.com/", WebUtils.extractBaseURLFromRequest(request));

    request = new MockHttpServletRequest("GET", "foo/bar");
    request.setServerName("www.test.com");
    request.setServerPort(123);
    assertEquals("http://www.test.com:123/", WebUtils.extractBaseURLFromRequest(request));
  }

}
