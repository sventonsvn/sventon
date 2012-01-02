/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
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
import org.sventon.TestUtils;
import org.sventon.model.DirEntry;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FileExtensionFilterTest {

  @Test
  public void testFilter() throws Exception {

    final List<DirEntry> list = TestUtils.getFileEntriesDirectoryList();

    List<DirEntry> filteredList;
    filteredList = new FileExtensionFilter("jpg").filter(list);
    assertEquals(1, filteredList.size());

    filteredList = new FileExtensionFilter("gif").filter(list);
    assertEquals(1, filteredList.size());

    try {
      new FileExtensionFilter(null).filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    try {
      new FileExtensionFilter("").filter(list);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }
  }

}