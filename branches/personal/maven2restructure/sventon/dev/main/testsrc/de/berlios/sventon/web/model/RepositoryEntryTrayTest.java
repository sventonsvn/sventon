package de.berlios.sventon.web.model;

import de.berlios.sventon.repository.RepositoryEntry;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;

import java.util.Date;

public class RepositoryEntryTrayTest extends TestCase {

  public void testEntryTray() {
    final RepositoryEntry entry = new RepositoryEntry(
        new SVNDirEntry(null, "file1.java", SVNNodeKind.FILE, 123, false, 1, new Date(), "jesper"), "/");

    final RepositoryEntryTray entryTray = new RepositoryEntryTray();

    assertEquals(0, entryTray.getSize());
    entryTray.add(entry);
    assertEquals(1, entryTray.getSize());

    entryTray.remove(entry);
    assertEquals(0, entryTray.getSize());
  }
}