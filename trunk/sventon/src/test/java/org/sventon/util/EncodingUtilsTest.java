package org.sventon.util;

import junit.framework.TestCase;

public class EncodingUtilsTest extends TestCase {

  public void testEncode() throws Exception {
    assertEquals("%2F", EncodingUtils.encode("/"));
    assertEquals("+", EncodingUtils.encode(" "));
  }

  public void testEncodeUrl() throws Exception {
    assertEquals("/%C3%BC.txt", EncodingUtils.encodeUrl("/\u00fc.txt"));
    assertEquals("/%C3%A5%C3%A4%C3%B6.txt", EncodingUtils.encodeUrl("/\u00e5\u00e4\u00f6.txt"));
  }

}