package de.berlios.sventon.web.model;

import de.berlios.sventon.repository.RepositoryEntry;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;

import java.util.ArrayList;
import java.util.List;

public class FileExtensionListTest extends TestCase {

  public void testGetExtensions() throws Exception {
    List<RepositoryEntry> entries = new ArrayList<RepositoryEntry>();

    assertEquals(0, new FileExtensionList(entries).getExtensions().size());

    entries.add(new RepositoryEntry(new SVNDirEntry(null, "test.abC", SVNNodeKind.FILE, 0, false, 0, null, null), "/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "test.jpg", SVNNodeKind.FILE, 0, false, 0, null, null), "/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "test.GIF", SVNNodeKind.FILE, 0, false, 0, null, null), "/"));

    assertEquals(3, new FileExtensionList(entries).getExtensions().size());

    entries.add(new RepositoryEntry(new SVNDirEntry(null, "anothertest.jpg", SVNNodeKind.FILE, 0, false, 0, null, null), "/"));

    assertEquals(3, new FileExtensionList(entries).getExtensions().size());
  }
}