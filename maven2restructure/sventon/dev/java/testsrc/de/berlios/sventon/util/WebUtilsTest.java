package de.berlios.sventon.util;

import static de.berlios.sventon.util.WebUtils.BR;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;

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

  public void testExtractServletNameFromRequest() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "log.svn");
    request.setServletPath("/svn/log.svn");
    assertEquals("log.svn", WebUtils.extractServletNameFromRequest(request));
  }
}
