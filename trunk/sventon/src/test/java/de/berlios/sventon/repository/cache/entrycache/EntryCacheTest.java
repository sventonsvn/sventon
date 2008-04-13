package de.berlios.sventon.repository.cache.entrycache;

import de.berlios.sventon.TestUtils;
import de.berlios.sventon.repository.RepositoryEntry;
import static de.berlios.sventon.repository.RepositoryEntry.Kind.*;
import junit.framework.TestCase;

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
    cache.add(TestUtils.getDirectoryList());
    assertEquals(13, cache.getSize());
    cache.clear();
    assertEquals(0, cache.getSize());
  }

  public void testEntryCacheAdd() throws Exception {
    final EntryCache cache = new MemoryCache();

    assertEquals(0, cache.getSize());
    cache.add(TestUtils.getDirectoryList());
    assertEquals(13, cache.getSize());
  }

  public void testFindEntry() throws Exception {
    final EntryCache cache = new MemoryCache();

    assertEquals(0, cache.getSize());
    cache.add(TestUtils.getDirectoryList());
    assertEquals(13, cache.getSize());

    assertEquals(2, cache.findEntry("tag", "/tags/").size());
  }

  public void testEntryCacheRemove() throws Exception {
    final EntryCache cache = new MemoryCache();

    assertEquals(0, cache.getSize());
    cache.add(TestUtils.getDirectoryList());
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
    cache.add(TestUtils.getDirectoryList());
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

  private void print(List<RepositoryEntry> entries) {
    for (RepositoryEntry entry : entries) {
      System.out.println(entry);
    }
  }
}
