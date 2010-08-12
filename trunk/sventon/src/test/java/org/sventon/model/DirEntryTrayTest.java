package org.sventon.model;

import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;

import java.util.Date;

public class DirEntryTrayTest extends TestCase {

  public void testEntryTray() throws Exception {
    final DirEntryTray entryTray = new DirEntryTray();
    SVNURL url = SVNURL.parseURIDecoded("http://localhost/");

    final PeggedDirEntry entry = new PeggedDirEntry(new DirEntry(
        new SVNDirEntry(null, url, "file1.java", SVNNodeKind.FILE, 123, false, 1, new Date(), "jesper"), "/"), 123);

    assertEquals(0, entryTray.getSize());
    assertTrue(entryTray.add(entry));
    assertEquals(1, entryTray.getSize());

    assertTrue(entryTray.remove(entry));
    assertEquals(0, entryTray.getSize());
  }

  public void testDuplicateEntries() throws Exception {
    final DirEntryTray entryTray = new DirEntryTray();
    SVNURL url = SVNURL.parseURIDecoded("http://localhost/");

    final DirEntry entry1 = new DirEntry(new SVNDirEntry(null, url, "file1.java",
        SVNNodeKind.FILE, 10, false, 1, new Date(), "jesper"), "/");

    final DirEntry entry1Duplicate = new DirEntry(new SVNDirEntry(null, url, "file1.java",
        SVNNodeKind.FILE, 10, false, 1, new Date(), "jesper"), "/");

    final DirEntry entry2 = new DirEntry(new SVNDirEntry(null, url, "file1.java",
        SVNNodeKind.FILE, 10, false, 2, new Date(), "jesper"), "/");

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