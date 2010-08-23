package org.sventon.model;

import junit.framework.TestCase;

import java.util.Date;

public class DirEntryTrayTest extends TestCase {

  public void testEntryTray() throws Exception {
    final DirEntryTray entryTray = new DirEntryTray();

    final PeggedDirEntry entry = new PeggedDirEntry(new DirEntry("/", "file1.java", "jesper", new Date(), DirEntry.Kind.FILE, 1, 123), 123);

    assertEquals(0, entryTray.getSize());
    assertTrue(entryTray.add(entry));
    assertEquals(1, entryTray.getSize());

    assertTrue(entryTray.remove(entry));
    assertEquals(0, entryTray.getSize());
  }

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