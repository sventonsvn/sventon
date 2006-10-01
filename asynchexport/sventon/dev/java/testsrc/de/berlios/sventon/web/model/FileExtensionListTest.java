package de.berlios.sventon.web.model;

import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;

import java.util.ArrayList;
import java.util.List;

import de.berlios.sventon.web.model.FileExtensionList;

public class FileExtensionListTest extends TestCase {

  public void testGetExtensions() throws Exception {
    List<SVNDirEntry> entries = new ArrayList<SVNDirEntry>();

    assertEquals(0, new FileExtensionList(entries).getExtensions().size());

    entries.add(new SVNDirEntry(null, "test.abC", SVNNodeKind.FILE, 0, false, 0, null, null));
    entries.add(new SVNDirEntry(null, "test.jpg", SVNNodeKind.FILE, 0, false, 0, null, null));
    entries.add(new SVNDirEntry(null, "test.GIF", SVNNodeKind.FILE, 0, false, 0, null, null));

    assertEquals(3, new FileExtensionList(entries).getExtensions().size());

    entries.add(new SVNDirEntry(null, "anothertest.jpg", SVNNodeKind.FILE, 0, false, 0, null, null));

    assertEquals(3, new FileExtensionList(entries).getExtensions().size());
  }
}