package de.berlios.sventon.repository.cache.entrycache;

import de.berlios.sventon.repository.RepositoryEntry;
import static de.berlios.sventon.repository.RepositoryEntry.Kind.*;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EntryCacheTest extends TestCase {

  public void testEntryCache() throws Exception {
    final EntryCache cache = new MemoryCache();

    assertEquals(0, cache.getCachedRevision());
    assertEquals(0, cache.getSize());
  }

  public void testEntryCacheClear() throws Exception {
    final EntryCache cache = new MemoryCache();

    assertEquals(0, cache.getSize());
    cache.add(getEntryTemplateList());
    assertEquals(11, cache.getSize());
    cache.clear();
    assertEquals(0, cache.getSize());
  }

  public void testEntryCacheAdd() throws Exception {
    final EntryCache cache = new MemoryCache();

    assertEquals(0, cache.getSize());
    cache.add(getEntryTemplateList());
    assertEquals(11, cache.getSize());
  }

  public void testEntryCacheRemove() throws Exception {
    final EntryCache cache = new MemoryCache();

    assertEquals(0, cache.getSize());
    cache.add(getEntryTemplateList());
    assertEquals(11, cache.getSize());

    cache.removeByName("/file1.java", false);
    assertEquals(10, cache.getSize());

    // Try to remove again
    cache.removeByName("/file1.java", false);
    assertEquals(10, cache.getSize());

    // Recursive must not matter in this case (entry is a file)
    cache.removeByName("/file2.html", true);
    assertEquals(9, cache.getSize());

    // Remove the 'trunk' recursively (trailing slash keeps the dir itself)
    cache.removeByName("/trunk/", true);
    assertEquals(5, cache.getSize());

    // Remove the 'tags' recursively (without trailing slash everything is deleted)
    cache.removeByName("/tags", true);
    assertEquals(2, cache.getSize());
  }

  public void testEntryCacheFindPattern() throws Exception {
    final EntryCache cache = new MemoryCache();

    assertEquals(0, cache.getSize());
    cache.add(getEntryTemplateList());
    assertEquals(11, cache.getSize());

    assertEquals(5, cache.findByPattern(".*[12].*", any, null).size());
    assertEquals(5, cache.findByPattern(".*[12].*", file, null).size());
    assertEquals(0, cache.findByPattern(".*[12].*", dir, null).size());

    assertEquals(5, cache.findByPattern(".*trunk.*", any, null).size());
    assertEquals(2, cache.findByPattern(".*trunk.*", dir, null).size());

    assertEquals(3, cache.findByPattern(".*", dir, null).size());

    assertEquals(1, cache.findByPattern(".*/trunk/src/.*", file, null).size());

    assertEquals(0, cache.findByPattern(".*/TrUnK/sRc/.*", file, null).size());
  }

  private List<RepositoryEntry> getEntryTemplateList() {
    final List<RepositoryEntry> entries = new ArrayList<RepositoryEntry>();
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "trunk", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"), "/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "tags", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"), "/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "file1.java", SVNNodeKind.FILE, 64000, false, 1, new Date(), "jesper"), "/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "file2.html", SVNNodeKind.FILE, 32000, false, 2, new Date(), "jesper"), "/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "File3.java", SVNNodeKind.FILE, 16000, false, 3, new Date(), "jesper"), "/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "src", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"), "/trunk/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "DirFile2.html", SVNNodeKind.FILE, 3200, false, 2, new Date(), "jesper"), "/trunk/src/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "file1_in_trunk.java", SVNNodeKind.FILE, 6400, false, 1, new Date(), "jesper"), "/trunk/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "DirFile3.java", SVNNodeKind.FILE, 1600, false, 3, new Date(), "jesper"), "/trunk/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "tagfile.txt", SVNNodeKind.FILE, 3200, false, 2, new Date(), "jesper"), "/tags/", null));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "tagfile2.txt", SVNNodeKind.FILE, 1600, false, 3, new Date(), "jesper"), "/tags/", null));
    return entries;
  }

  private void print(List<RepositoryEntry> entries) {
    for (RepositoryEntry entry : entries) {
      System.out.println(entry);
    }
  }
}