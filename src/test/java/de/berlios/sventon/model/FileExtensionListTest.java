package de.berlios.sventon.model;

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

    entries.add(createDirEntry("test.abC"));
    entries.add(createDirEntry("test.jpg"));
    entries.add(createDirEntry("test.GIF"));

    assertEquals(3, new FileExtensionList(entries).getExtensions().size());

    entries.add(createDirEntry("anothertest.jpg"));

    assertEquals(3, new FileExtensionList(entries).getExtensions().size());
  }

    private RepositoryEntry createDirEntry(final String name) {
        return new RepositoryEntry(new SVNDirEntry(null, null, name, SVNNodeKind.FILE, 0, false, 0, null, null), "/");
    }
}
