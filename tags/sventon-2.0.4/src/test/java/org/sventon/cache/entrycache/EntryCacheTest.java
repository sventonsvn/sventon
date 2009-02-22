package org.sventon.cache.entrycache;

import junit.framework.TestCase;
import org.sventon.TestUtils;
import org.sventon.cache.CacheException;
import org.sventon.model.RepositoryEntry;
import static org.sventon.model.RepositoryEntry.Kind.*;

import java.util.List;
import java.util.regex.Pattern;

public class EntryCacheTest extends TestCase {

  public void testEntryCache() throws Exception {
    final EntryCache cache = createCache();
    assertEquals(0, cache.getCachedRevision());
    assertEquals(0, cache.getSize());
  }

  private EntryCache createCache() throws CacheException {
    final EntryCache cache = new MemoryCache();
    cache.init();
    return cache;
  }

  public void testEntryCacheClear() throws Exception {
    final EntryCache cache = createCache();
    assertEquals(0, cache.getSize());
    cache.add(TestUtils.getDirectoryList());
    assertEquals(13, cache.getSize());
    cache.clear();
    assertEquals(0, cache.getSize());
  }

  public void testEntryCacheAdd() throws Exception {
    final EntryCache cache = createCache();
    assertEquals(0, cache.getSize());
    cache.add(TestUtils.getDirectoryList());
    assertEquals(13, cache.getSize());
  }

  public void testFindEntry() throws Exception {
    final EntryCache cache = createCache();
    assertEquals(0, cache.getSize());
    cache.add(TestUtils.getDirectoryList());
    assertEquals(13, cache.getSize());

    assertEquals(2, cache.findEntries("tag", "/tags/", false).size());
  }

  public void testEntryCacheRemove() throws Exception {
    final EntryCache cache = createCache();
    assertEquals(0, cache.getSize());
    cache.add(TestUtils.getDirectoryList());
    assertEquals(13, cache.getSize());

    cache.removeEntry("/file1.java", false);
    assertEquals(12, cache.getSize());

    // Try to remove again
    cache.removeEntry("/file1.java", false);
    assertEquals(12, cache.getSize());

    // Recursive must not matter in this case (entry is a file)
    cache.removeEntry("/file2.html", true);
    assertEquals(11, cache.getSize());

    // Remove the 'trunk' recursively (trailing slash keeps the dir itself)
    cache.removeEntry("/trunk/", true);
    assertEquals(4, cache.getSize());

    // Remove the 'tags' recursively (without trailing slash everything is deleted)
    cache.removeEntry("/tags", true);
    assertEquals(1, cache.getSize());
  }

  public void testEntryCacheFindPattern() throws Exception {
    final EntryCache cache = createCache();
    assertEquals(0, cache.getSize());
    cache.add(TestUtils.getDirectoryList());
    assertEquals(13, cache.getSize());

    assertEquals(5, cache.findEntriesByPattern(Pattern.compile(".*[12].*"), ANY).size());
    assertEquals(5, cache.findEntriesByPattern(Pattern.compile(".*[12].*"), FILE).size());
    assertEquals(0, cache.findEntriesByPattern(Pattern.compile(".*[12].*"), DIR).size());

    assertEquals(8, cache.findEntriesByPattern(Pattern.compile(".*trunk.*"), ANY).size());
    assertEquals(3, cache.findEntriesByPattern(Pattern.compile(".*trunk.*"), DIR).size());

    assertEquals(4, cache.findEntriesByPattern(Pattern.compile(".*"), DIR).size());

    assertEquals(1, cache.findEntriesByPattern(Pattern.compile(".*/trunk/src/.*"), FILE).size());

    assertEquals(1, cache.findEntriesByPattern(Pattern.compile(".*/TrUnK/sRc/.*", Pattern.CASE_INSENSITIVE), FILE).size());
  }

  private void print(List<RepositoryEntry> entries) {
    for (RepositoryEntry entry : entries) {
      System.out.println(entry);
    }
  }
}
