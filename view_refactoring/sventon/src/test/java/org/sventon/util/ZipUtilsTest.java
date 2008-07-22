package org.sventon.util;

import junit.framework.TestCase;

public class ZipUtilsTest extends TestCase {

  public void testZipUtils() throws Exception {
    try {
      new ZipUtils(null);
      fail("Should throw IllegalArgumentException");
    } catch (Exception e) {
      // expected
    }
  }
}