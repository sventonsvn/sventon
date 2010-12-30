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

import java.util.Date;

import static org.junit.Assert.*;

public class DirEntryTrayTest {

  @Test
  public void testEntryTray() throws Exception {
    final DirEntryTray entryTray = new DirEntryTray();

    final PeggedDirEntry entry = new PeggedDirEntry(new DirEntry("/", "file1.java", "jesper", new Date(), DirEntry.Kind.FILE, 1, 123), 123);

    assertEquals(0, entryTray.getSize());
    assertTrue(entryTray.add(entry));
    assertEquals(1, entryTray.getSize());

    assertTrue(entryTray.remove(entry));
    assertEquals(0, entryTray.getSize());
  }

  @Test
  public void testDuplicateEntries() throws Exception {
    final DirEntryTray entryTray = new DirEntryTray();

    final Date date = new Date();
    final DirEntry entry1 = new DirEntry("/", "file1.java", "jesper", date, DirEntry.Kind.FILE, 1, 10);
    final DirEntry entry1Duplicate = new DirEntry("/", "file1.java", "jesper", date, DirEntry.Kind.FILE, 1, 10);
    final DirEntry entry2 = new DirEntry("/", "file1.java", "jesper", date, DirEntry.Kind.FILE, 2, 10);

    assertEquals(0, entryTray.getSize());
    assertTrue(entryTray.add(new PeggedDirEntry(entry1, -1)));
    assertEquals(1, entryTray.getSize());
    assertFalse(entryTray.add(new PeggedDirEntry(entry1Duplicate, -1)));
    assertEquals(1, entryTray.getSize());
    assertTrue(entryTray.add(new PeggedDirEntry(entry2, -1)));
    assertEquals(2, entryTray.getSize());

    entryTray.removeAll();
    assertEquals(0, entryTray.getSize());
  }
}