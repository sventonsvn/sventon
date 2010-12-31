/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
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