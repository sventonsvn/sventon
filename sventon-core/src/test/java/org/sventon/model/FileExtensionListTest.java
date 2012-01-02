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
package org.sventon.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FileExtensionListTest {

  @Test
  public void testGetExtensions() throws Exception {
    assertEquals(0, new FileExtensionList(new ArrayList<DirEntry>()).getExtensions().size());
    final List<DirEntry> entries = getFileEntriesDirectoryList();
    assertEquals(3, new FileExtensionList(entries).getExtensions().size());
    entries.add(new DirEntry("/", "anothertest.jpg", "", null, DirEntry.Kind.FILE, 0, 0));
    assertEquals(3, new FileExtensionList(entries).getExtensions().size());
  }

  private static List<DirEntry> getFileEntriesDirectoryList() {
    final List<DirEntry> list = new ArrayList<DirEntry>();
    list.add(new DirEntry("/", "test.abC", "", null, DirEntry.Kind.FILE, 0, 0));
    list.add(new DirEntry("/", "test.jpg", "", null, DirEntry.Kind.FILE, 0, 0));
    list.add(new DirEntry("/", "test.GIF", "", null, DirEntry.Kind.FILE, 0, 0));
    return list;
  }
}