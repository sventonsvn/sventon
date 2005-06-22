package de.berlios.sventon.svnsupport;

import junit.framework.TestCase;
import org.tmatesoft.svn.core.io.SVNDirEntry;

import java.util.Date;

public class IndexEntryTest extends TestCase {

  public void testGetFriendlyFullEntryNameLongFullName() throws Exception {
    SVNDirEntry e = new SVNDirEntry("test.fil", null, 1, false, 1, new Date(), "A");
    IndexEntry indexEntry = new IndexEntry(e, "/source/com/bli/bla/blu/saaaaaaaaaaaaaaaaaaaaaaaaaaaa/");
    assertEquals("/source/com/bli/bla/blu/saaaaaaaaaaaa..../test.fil", indexEntry.getFriendlyFullEntryName());
    assertEquals(50, indexEntry.getFriendlyFullEntryName().length());

    e = new SVNDirEntry("reallylongfilenamejustfortestingpurposes.fil", null, 1, false, 1, new Date(), "A");
    indexEntry = new IndexEntry(e, "/source/com/bli/bla/blu/saaaaa/bbbbbb/ccccccccc/dddddddd/aaaaaaaaaaaaaaaaaa/");
    assertEquals("/..../reallylongfilenamejustfortestingpurposes.fil", indexEntry.getFriendlyFullEntryName());
    assertEquals(50, indexEntry.getFriendlyFullEntryName().length());
  }

  public void testGetFriendlyFullEntryNameShortName() throws Exception {
    SVNDirEntry e = new SVNDirEntry("test.fil", null, 1, false, 1, new Date(), "A");
    IndexEntry indexEntry = new IndexEntry(e, "/source/com/bli/bla/blu/");
    assertEquals("/source/com/bli/bla/blu/test.fil", indexEntry.getFriendlyFullEntryName());
  }

  public void testGetFriendlyFullEntryNameLongName() throws Exception {
    SVNDirEntry e = new SVNDirEntry("thisisafilenamewithmorethanfiftycharactersinitreallynotperfect.fil", null, 1, false, 1, new Date(), "A");
    IndexEntry indexEntry = new IndexEntry(e, "/source/com/bli/bla/blu/");
    assertEquals("/thisisafilenamewithmorethanfiftycharactersinitreallynotperfect.fil", indexEntry.getFriendlyFullEntryName());

    e = new SVNDirEntry("reallylongfilenamejustfortestingpurposesonly.fil", null, 1, false, 1, new Date(), "A");
    indexEntry = new IndexEntry(e, "/source/com/bli/bla/blu/saaaaa/bbbbbb/ccccccccc/dddddddd/aaaaaaaaaaaaaaaaaa/");
    assertEquals("/reallylongfilenamejustfortestingpurposesonly.fil", indexEntry.getFriendlyFullEntryName());
    assertEquals(49, indexEntry.getFriendlyFullEntryName().length());
  }

}