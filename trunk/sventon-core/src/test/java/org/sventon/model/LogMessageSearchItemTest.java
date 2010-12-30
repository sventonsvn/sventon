/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.model;

import org.junit.Test;

import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

public class LogMessageSearchItemTest {

  @Test
  public void testToStringList() throws Exception {
    final SortedSet<ChangedPath> paths = new TreeSet<ChangedPath>();
    paths.add(new ChangedPath("/trunk", null, -1, ChangeType.ADDED));
    paths.add(new ChangedPath("/tags", null, -1, ChangeType.ADDED));
    assertEquals("[/tags, /trunk]", LogMessageSearchItem.toStringList(paths).toString());
  }

}
