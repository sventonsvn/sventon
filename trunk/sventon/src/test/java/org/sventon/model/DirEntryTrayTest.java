package org.sventon.model;

import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;

import java.util.Date;

public class DirEntryTrayTest extends TestCase {

  public void testEntryTray() throws SVNException {
    final DirEntryTray entryTray = new DirEntryTray();
    SVNURL url = SVNURL.parseURIDecoded("http://localhost/");

    final PeggedRepositoryEntry entry = new PeggedRepositoryEntry(new RepositoryEntry(
        new SVNDirEntry(null, url, "file1.java", SVNNodeKind.FILE, 123, false, 1, new Date(), "jesper"), "/"), 123);

    assertEquals(0, entryTray.getSize());
    assertTrue(entryTray.add(entry));
    assertEquals(1, entryTray.getSize());

    assertTrue(entryTray.remove(entry));
    assertEquals(0, entryTray.getSize());
  }

  public void testDuplicateEntries() throws SVNException {
    final DirEntryTray entryTray = new DirEntryTray();
    SVNURL url = SVNURL.parseURIDecoded("http://localhost/");

    final RepositoryEntry entry1 = new RepositoryEntry(new SVNDirEntry(null, url, "file1.java",
        SVNNodeKind.FILE, 10, false, 1, new Date(), "jesper"), "/");

    final RepositoryEntry entry1Duplicate = new RepositoryEntry(new SVNDirEntry(null, url, "file1.java",
        SVNNodeKind.FILE, 10, false, 1, new Date(), "jesper"), "/");

    final RepositoryEntry entry2 = new RepositoryEntry(new SVNDirEntry(null, url, "file1.java",
        SVNNodeKind.FILE, 10, false, 2, new Date(), "jesper"), "/");

    assertEquals(0, entryTray.getSize());
    assertTrue(entryTray.add(new PeggedRepositoryEntry(entry1, -1)));
    assertEquals(1, entryTray.getSize());
    assertFalse(entryTray.add(new PeggedRepositoryEntry(entry1Duplicate, -1)));
    assertEquals(1, entryTray.getSize());
    assertTrue(entryTray.add(new PeggedRepositoryEntry(entry2, -1)));
    assertEquals(2, entryTray.getSize());

    entryTray.removeAll();
    assertEquals(0, entryTray.getSize());
  }
}