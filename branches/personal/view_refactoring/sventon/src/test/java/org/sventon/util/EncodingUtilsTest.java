package org.sventon.util;

import junit.framework.TestCase;

public class EncodingUtilsTest extends TestCase {

  public void testEncode() throws Exception {
    assertEquals("%2F", EncodingUtils.encode("/"));
    assertEquals("+", EncodingUtils.encode(" "));
  }

  public void testEncodeUrl() throws Exception {
    assertEquals("/%C3%BC.txt", EncodingUtils.encodeUrl("/ь.txt"));
    assertEquals("/%C3%A5%C3%A4%C3%B6.txt", EncodingUtils.encodeUrl("/едц.txt"));
  }

}