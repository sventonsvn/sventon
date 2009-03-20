package org.sventon.cache.entrycache;

import junit.framework.TestCase;
import org.sventon.TestUtils;
import org.sventon.cache.CacheException;
import org.sventon.model.RepositoryEntry;

import java.io.File;
import java.util.List;

public class EntryCacheTest extends TestCase {

  private void addAll(EntryCache entryCache, List<RepositoryEntry> entries) {
    for (RepositoryEntry repositoryEntry : entries) {
      entryCache.add(repositoryEntry);
    }
  }

  public void testEntryCache() throws Exception {
    final EntryCache cache = createCache();
    assertEquals(0, cache.getLatestCachedRevisionNumber());
    assertEquals(0, cache.getSize());
  }

  private EntryCache createCache() throws CacheException {
    final EntryCache cache = new EntryCacheImpl(new File("test"));
    cache.init();
    return cache;
  }

  public void testEntryCacheClear() throws Exception {
    final EntryCache entryCache = createCache();
    assertEquals(0, entryCache.getSize());
    addAll(entryCache, TestUtils.getDirectoryList());
    assertEquals(13, entryCache.getSize());
    entryCache.clear();
    assertEquals(0, entryCache.getSize());
  }

  public void testEntryCacheAdd() throws Exception {
    final EntryCache entryCache = createCache();
    assertEquals(0, entryCache.getSize());
    addAll(entryCache, TestUtils.getDirectoryList());
    assertEquals(13, entryCache.getSize());
  }

  public void testFindEntry() throws Exception {
    final EntryCache entryCache = createCache();
    assertEquals(0, entryCache.getSize());
    addAll(entryCache, TestUtils.getDirectoryList());
    assertEquals(13, entryCache.getSize());

    assertEquals(2, entryCache.findEntries("tag", "/tags/", false).size());
  }

  public void testEntryCacheRemove() throws Exception {
    final EntryCache entryCache = createCache();
    assertEquals(0, entryCache.getSize());
    addAll(entryCache, TestUtils.getDirectoryList());
    assertEquals(13, entryCache.getSize());

    entryCache.removeEntry("/file1.java", false);
    assertEquals(12, entryCache.getSize());

    // Try to remove again
    entryCache.removeEntry("/file1.java", false);
    assertEquals(12, entryCache.getSize());

    // Recursive must not matter in this case (entry is a file)
    entryCache.removeEntry("/file2.html", true);
    assertEquals(11, entryCache.getSize());

    // Remove the 'trunk' recursively (trailing slash keeps the dir itself)
    entryCache.removeEntry("/trunk/", true);
    assertEquals(4, entryCache.getSize());

    // Remove the 'tags' recursively (without trailing slash everything is deleted)
    entryCache.removeEntry("/tags", true);
    assertEquals(1, entryCache.getSize());

  }

//  public void testEntryCacheFindPattern() throws Exception {
//    final EntryCache cache = createCache();
//    assertEquals(0, cache.getSize());
//    cache.add(TestUtils.getDirectoryList());
//    assertEquals(13, cache.getSize());
//
//    assertEquals(5, cache.findEntriesByPattern(Pattern.compile(".*[12].*"), ANY).size());
//    assertEquals(5, cache.findEntriesByPattern(Pattern.compile(".*[12].*"), FILE).size());
//    assertEquals(0, cache.findEntriesByPattern(Pattern.compile(".*[12].*"), DIR).size());
//
//    assertEquals(8, cache.findEntriesByPattern(Pattern.compile(".*trunk.*"), ANY).size());
//    assertEquals(3, cache.findEntriesByPattern(Pattern.compile(".*trunk.*"), DIR).size());
//
//    assertEquals(4, cache.findEntriesByPattern(Pattern.compile(".*"), DIR).size());
//
//    assertEquals(1, cache.findEntriesByPattern(Pattern.compile(".*/trunk/src/.*"), FILE).size());
//
//    assertEquals(1, cache.findEntriesByPattern(Pattern.compile(".*/TrUnK/sRc/.*", Pattern.CASE_INSENSITIVE), FILE).size());
//  }

  private void print(List<RepositoryEntry> entries) {
    for (RepositoryEntry entry : entries) {
      System.out.println(entry);
    }
  }
}
