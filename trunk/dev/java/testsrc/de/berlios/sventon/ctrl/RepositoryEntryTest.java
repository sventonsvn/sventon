package de.berlios.sventon.ctrl;

import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;

import java.util.Date;

public class RepositoryEntryTest extends TestCase {

  public void testGetFriendlyFullEntryNameLongFullName() throws Exception {
    SVNDirEntry e = new SVNDirEntry(null, "test.fil", SVNNodeKind.FILE, 1, false, 1, new Date(), "A");
    RepositoryEntry indexEntry = new RepositoryEntry(e, "/source/com/bli/bla/blu/saaaaaaaaaaaaaaaaaaaaaaaaaaaa/", null);
    assertEquals(".../bla/blu/saaaaaaaaaaaaaaaaaaaaaaaaaaaa/test.fil", indexEntry.getFriendlyFullEntryName());
    assertEquals(50, indexEntry.getFriendlyFullEntryName().length());

    e = new SVNDirEntry(null, "reallylongfilenamejustfortestingpurposes.fil", SVNNodeKind.FILE, 1, false, 1, new Date(), "A");
    indexEntry = new RepositoryEntry(e, "/source/com/bli/bla/blu/saaaaa/bbbbbb/ccccccccc/dddddddd/aaaaaaaaaaaaaaaaaa/", null);
    assertEquals("...aa/reallylongfilenamejustfortestingpurposes.fil", indexEntry.getFriendlyFullEntryName());
    assertEquals(50, indexEntry.getFriendlyFullEntryName().length());
  }

  public void testGetFriendlyFullEntryNameShortName() throws Exception {
    SVNDirEntry e = new SVNDirEntry(null, "test.fil", SVNNodeKind.FILE, 1, false, 1, new Date(), "A");
    RepositoryEntry indexEntry = new RepositoryEntry(e, "/source/com/bli/bla/blu/", null);
    assertEquals("/source/com/bli/bla/blu/test.fil", indexEntry.getFriendlyFullEntryName());
  }

  public void testGetFriendlyFullEntryNameLongName() throws Exception {
    SVNDirEntry e = new SVNDirEntry(null, "thisisafilenamewithmorethanfiftycharactersinitreallynotperfect.fil", SVNNodeKind.FILE, 1,
        false, 1, new Date(), "A");
    RepositoryEntry indexEntry = new RepositoryEntry(e, "/source/com/bli/bla/blu/", null);
    assertEquals("...morethanfiftycharactersinitreallynotperfect.fil", indexEntry.getFriendlyFullEntryName());
    assertEquals(50, indexEntry.getFriendlyFullEntryName().length());

    e = new SVNDirEntry(null, "reallylongfilenamejustfortestingpurposesonly.fil", SVNNodeKind.FILE, 1, false, 1, new Date(), "A");
    indexEntry = new RepositoryEntry(e, "/source/com/bli/bla/blu/saaaaa/bbbbbb/ccccccccc/dddddddd/aaaaaaaaaaaaaaaaaa/", null);
    assertEquals("...eallylongfilenamejustfortestingpurposesonly.fil", indexEntry.getFriendlyFullEntryName());
    assertEquals(50, indexEntry.getFriendlyFullEntryName().length());
  }

}