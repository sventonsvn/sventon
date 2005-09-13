package de.berlios.sventon.ctrl;

import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNDirEntry;

import java.util.Date;

public class RepositoryEntryTest extends TestCase {

  public void testGetFriendlyFullEntryNameLongFullName() throws Exception {
    SVNDirEntry e = new SVNDirEntry("test.fil", null, 1, false, 1, new Date(), "A");
    RepositoryEntry indexEntry = new RepositoryEntry(e, "/source/com/bli/bla/blu/saaaaaaaaaaaaaaaaaaaaaaaaaaaa/");
    assertEquals(".../bla/blu/saaaaaaaaaaaaaaaaaaaaaaaaaaaa/test.fil", indexEntry.getFriendlyFullEntryName());
    assertEquals(50, indexEntry.getFriendlyFullEntryName().length());

    e = new SVNDirEntry("reallylongfilenamejustfortestingpurposes.fil", null, 1, false, 1, new Date(), "A");
    indexEntry = new RepositoryEntry(e, "/source/com/bli/bla/blu/saaaaa/bbbbbb/ccccccccc/dddddddd/aaaaaaaaaaaaaaaaaa/");
    assertEquals("...aa/reallylongfilenamejustfortestingpurposes.fil", indexEntry.getFriendlyFullEntryName());
    assertEquals(50, indexEntry.getFriendlyFullEntryName().length());
  }

  public void testGetFriendlyFullEntryNameShortName() throws Exception {
    SVNDirEntry e = new SVNDirEntry("test.fil", null, 1, false, 1, new Date(), "A");
    RepositoryEntry indexEntry = new RepositoryEntry(e, "/source/com/bli/bla/blu/");
    assertEquals("/source/com/bli/bla/blu/test.fil", indexEntry.getFriendlyFullEntryName());
  }

  public void testGetFriendlyFullEntryNameLongName() throws Exception {
    SVNDirEntry e = new SVNDirEntry("thisisafilenamewithmorethanfiftycharactersinitreallynotperfect.fil", null, 1,
        false, 1, new Date(), "A");
    RepositoryEntry indexEntry = new RepositoryEntry(e, "/source/com/bli/bla/blu/");
    assertEquals("...morethanfiftycharactersinitreallynotperfect.fil", indexEntry.getFriendlyFullEntryName());
    assertEquals(50, indexEntry.getFriendlyFullEntryName().length());

    e = new SVNDirEntry("reallylongfilenamejustfortestingpurposesonly.fil", null, 1, false, 1, new Date(), "A");
    indexEntry = new RepositoryEntry(e, "/source/com/bli/bla/blu/saaaaa/bbbbbb/ccccccccc/dddddddd/aaaaaaaaaaaaaaaaaa/");
    assertEquals("...eallylongfilenamejustfortestingpurposesonly.fil", indexEntry.getFriendlyFullEntryName());
    assertEquals(50, indexEntry.getFriendlyFullEntryName().length());
  }

  public void testGetFullEntryNameStripMountPoint() throws Exception {
    SVNDirEntry entry = new SVNDirEntry("test.file", null, 1, false, 1, new Date(), "A");
    RepositoryEntry repositoryEntry = new RepositoryEntry(entry, "/trunk/dir/", "/trunk");

    assertEquals("/trunk/dir/test.file", repositoryEntry.getFriendlyFullEntryName());
    assertEquals("/trunk/dir/test.file", repositoryEntry.getFullEntryName());
    assertEquals("/dir/test.file", repositoryEntry.getFullEntryNameStripMountPoint());

    repositoryEntry = new RepositoryEntry(entry, "/trunk/dir/");
    assertEquals("/trunk/dir/test.file", repositoryEntry.getFullEntryNameStripMountPoint());
  }

}