package org.sventon.util;

import junit.framework.TestCase;

public class EncodingUtilsTest extends TestCase {

  public void testEncode() throws Exception {
    assertEquals("%2F", EncodingUtils.encode("/"));
    assertEquals("%2B", EncodingUtils.encode("+"));
    assertEquals("%23", EncodingUtils.encode("#"));
    assertEquals("+", EncodingUtils.encode(" "));
  }

  public void testEncodeUrl() throws Exception {
    assertEquals("/test/%C3%BC.txt", EncodingUtils.encodeUrl("/test/\u00fc.txt"));
    assertEquals("/%C3%A5%C3%A4%C3%B6.txt", EncodingUtils.encodeUrl("/\u00e5\u00e4\u00f6.txt"));
    assertEquals("/%2B/test.txt", EncodingUtils.encodeUrl("/+/test.txt"));
    assertEquals("/%23/test.txt", EncodingUtils.encodeUrl("/#/test.txt"));
    assertEquals("+", EncodingUtils.encodeUrl(" "));
    assertEquals("", EncodingUtils.encodeUrl(""));

    try {
      EncodingUtils.encodeUrl(null);
      fail("Should cause NPE");
    } catch (Exception e) {
      // expected
    }
  }

}