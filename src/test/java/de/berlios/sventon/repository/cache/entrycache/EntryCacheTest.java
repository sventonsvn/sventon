package de.berlios.sventon.repository.cache.entrycache;

import de.berlios.sventon.repository.RepositoryEntry;
import static de.berlios.sventon.repository.RepositoryEntry.Kind.*;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

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
    assertEquals(13, cache.getSize());
    cache.clear();
    assertEquals(0, cache.getSize());
  }

  public void testEntryCacheAdd() throws Exception {
    final EntryCache cache = new MemoryCache();

    assertEquals(0, cache.getSize());
    cache.add(getEntryTemplateList());
    assertEquals(13, cache.getSize());
  }

  public void testFindEntry() throws Exception {
    final EntryCache cache = new MemoryCache();

    assertEquals(0, cache.getSize());
    cache.add(getEntryTemplateList());
    assertEquals(13, cache.getSize());

    assertEquals(2, cache.findEntry("tag", "/tags/").size());
  }

  public void testEntryCacheRemove() throws Exception {
    final EntryCache cache = new MemoryCache();

    assertEquals(0, cache.getSize());
    cache.add(getEntryTemplateList());
    assertEquals(13, cache.getSize());

    cache.removeByName("/file1.java", false);
    assertEquals(12, cache.getSize());

    // Try to remove again
    cache.removeByName("/file1.java", false);
    assertEquals(12, cache.getSize());

    // Recursive must not matter in this case (entry is a file)
    cache.removeByName("/file2.html", true);
    assertEquals(11, cache.getSize());

    // Remove the 'trunk' recursively (trailing slash keeps the dir itself)
    cache.removeByName("/trunk/", true);
    assertEquals(4, cache.getSize());

    // Remove the 'tags' recursively (without trailing slash everything is deleted)
    cache.removeByName("/tags", true);
    assertEquals(1, cache.getSize());
  }

  public void testEntryCacheFindPattern() throws Exception {
    final EntryCache cache = new MemoryCache();

    assertEquals(0, cache.getSize());
    cache.add(getEntryTemplateList());
    assertEquals(13, cache.getSize());

    assertEquals(5, cache.findByPattern(Pattern.compile(".*[12].*"), any).size());
    assertEquals(5, cache.findByPattern(Pattern.compile(".*[12].*"), file).size());
    assertEquals(0, cache.findByPattern(Pattern.compile(".*[12].*"), dir).size());

    assertEquals(8, cache.findByPattern(Pattern.compile(".*trunk.*"), any).size());
    assertEquals(3, cache.findByPattern(Pattern.compile(".*trunk.*"), dir).size());

    assertEquals(4, cache.findByPattern(Pattern.compile(".*"), dir).size());

    assertEquals(1, cache.findByPattern(Pattern.compile(".*/trunk/src/.*"), file).size());

    assertEquals(1, cache.findByPattern(Pattern.compile(".*/TrUnK/sRc/.*", Pattern.CASE_INSENSITIVE), file).size());
  }

  private List<RepositoryEntry> getEntryTemplateList() {
    final List<RepositoryEntry> entries = new ArrayList<RepositoryEntry>();
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "trunk", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"), "/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "tags", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"), "/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "file1.java", SVNNodeKind.FILE, 64000, false, 1, new Date(), "jesper"), "/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "file2.html", SVNNodeKind.FILE, 32000, false, 2, new Date(), "jesper"), "/"));

    entries.add(new RepositoryEntry(new SVNDirEntry(null, "tagfile.txt", SVNNodeKind.FILE, 3200, false, 2, new Date(), "jesper"), "/tags/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "tagfile2.txt", SVNNodeKind.FILE, 1600, false, 3, new Date(), "jesper"), "/tags/"));

    entries.add(new RepositoryEntry(new SVNDirEntry(null, "code", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"), "/trunk/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "src", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"), "/trunk/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "DirFile3.java", SVNNodeKind.FILE, 1600, false, 3, new Date(), "jesper"), "/trunk/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "file1_in_trunk.java", SVNNodeKind.FILE, 6400, false, 1, new Date(), "jesper"), "/trunk/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "TestDirFile3.java", SVNNodeKind.FILE, 1600, false, 3, new Date(), "jesper"), "/trunk/"));

    entries.add(new RepositoryEntry(new SVNDirEntry(null, "File3.java", SVNNodeKind.FILE, 16000, false, 3, new Date(), "jesper"), "/trunk/code/"));
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "DirFile2.html", SVNNodeKind.FILE, 3200, false, 2, new Date(), "jesper"), "/trunk/src/"));
    return entries;
  }

  private void print(List<RepositoryEntry> entries) {
    for (RepositoryEntry entry : entries) {
      System.out.println(entry);
    }
  }
}
