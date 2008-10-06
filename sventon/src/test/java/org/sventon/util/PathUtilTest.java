package org.sventon.util;

import junit.framework.TestCase;

public class PathUtilTest extends TestCase {

  public void testGetTarget() {
    assertEquals("File.java", PathUtil.getTarget("trunk/src/File.java"));
    assertEquals("src", PathUtil.getTarget("trunk/src/"));
    assertEquals("src", PathUtil.getTarget("trunk/src"));
    assertEquals("", PathUtil.getTarget(""));
    assertEquals("", PathUtil.getTarget("/"));
    assertEquals("", PathUtil.getTarget(null));
  }

}