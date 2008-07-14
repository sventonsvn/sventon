package org.sventon.model;

import org.sventon.TestUtils;
import org.sventon.model.RepositoryEntry;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;

import java.util.ArrayList;
import java.util.List;

public class FileExtensionListTest extends TestCase {

  public void testGetExtensions() throws Exception {
    assertEquals(0, new FileExtensionList(new ArrayList<RepositoryEntry>()).getExtensions().size());

    final List<RepositoryEntry> entries = TestUtils.getFileEntriesDirectoryList();

    assertEquals(3, new FileExtensionList(entries).getExtensions().size());

    entries.add(new RepositoryEntry(new SVNDirEntry(null, "anothertest.jpg", SVNNodeKind.FILE, 0, false, 0, null, null), "/"));

    assertEquals(3, new FileExtensionList(entries).getExtensions().size());
  }
}