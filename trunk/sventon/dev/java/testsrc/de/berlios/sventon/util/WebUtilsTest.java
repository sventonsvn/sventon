package de.berlios.sventon.util;

import junit.framework.TestCase;

public class WebUtilsTest extends TestCase {

  public void testNl2br() throws Exception {
    assertEquals("one<br/>two", WebUtils.nl2br("one\ntwo"));
  }

}
