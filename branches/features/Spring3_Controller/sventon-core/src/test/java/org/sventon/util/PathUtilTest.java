package org.sventon.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PathUtilTest {

  @Test
  public void testGetTarget() {
    assertEquals("File.java", PathUtil.getTarget("trunk/src/File.java"));
    assertEquals("src", PathUtil.getTarget("trunk/src/"));
    assertEquals("src", PathUtil.getTarget("trunk/src"));
    assertEquals("", PathUtil.getTarget(""));
    assertEquals("", PathUtil.getTarget("/"));
    assertEquals("", PathUtil.getTarget(null));
  }

}