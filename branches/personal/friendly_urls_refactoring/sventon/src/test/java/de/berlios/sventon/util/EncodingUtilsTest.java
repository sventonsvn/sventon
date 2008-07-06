package de.berlios.sventon.util;

import junit.framework.TestCase;

public class EncodingUtilsTest extends TestCase {

  public void testEncode() throws Exception {
    assertEquals("%2F", EncodingUtils.encode("/"));
    assertEquals("+", EncodingUtils.encode(" "));
  }
}