package org.sventon.cache.entrycache;

import junit.framework.TestCase;
import org.sventon.TestUtils;
import org.sventon.model.CamelCasePattern;
import org.sventon.model.RepositoryEntry;

import java.io.File;
import java.util.List;

public class EntryCacheTest extends TestCase {

  private EntryCache entryCache;

  @Override
  protected void setUp() throws Exception {
    entryCache = new EntryCacheImpl(new File("."));
    entryCache.init();
  }

  @Override
  protected void tearDown() throws Exception {
    entryCache.shutdown();
  }

  private void addAll(EntryCache entryCache, List<RepositoryEntry> entries) {
    entryCache.add(entries.toArray(new RepositoryEntry[entries.size()]));
  }

  public void testEntryCache() throws Exception {
    assertEquals(0, entryCache.getLatestCachedRevisionNumber());
    assertEquals(0, entryCache.getSize());
  }

  public void testEntryCacheClear() throws Exception {
    assertEquals(0, entryCache.getSize());
    addAll(entryCache, TestUtils.getDirectoryList());
    assertEquals(13, entryCache.getSize());
    entryCache.clear();
    assertEquals(0, entryCache.getSize());
  }

  public void testEntryCacheAdd() throws Exception {
    assertEquals(0, entryCache.getSize());
    addAll(entryCache, TestUtils.getDirectoryList());
    assertEquals(13, entryCache.getSize());
  }

  public void testFindEntry() throws Exception {
    assertEquals(0, entryCache.getSize());
    addAll(entryCache, TestUtils.getDirectoryList());
    assertEquals(13, entryCache.getSize());

    assertEquals(2, entryCache.findEntries("tag", "/tags/").size());
    assertEquals(2, entryCache.findEntries("tag", "/TAGS/").size());
  }

  public void testFindEntryByAuthor() throws Exception {
    assertEquals(0, entryCache.getSize());
    addAll(entryCache, TestUtils.getDirectoryList());
    assertEquals(13, entryCache.getSize());

    assertEquals(11, entryCache.findEntries("jesper", "/").size());
    assertEquals(2, entryCache.findEntries("jesper", "/tags/").size());
  }

  public void testEntryCacheRemove() throws Exception {
    assertEquals(0, entryCache.getSize());
    addAll(entryCache, TestUtils.getDirectoryList());
    assertEquals(13, entryCache.getSize());

    entryCache.remove("/file1.java");
    assertEquals(12, entryCache.getSize());

    // Try to remove again
    entryCache.remove("/file1.java");
    assertEquals(12, entryCache.getSize());

    // Must not matter in this case (entry is a file)
    entryCache.removeDirectory("/file2.html");
    assertEquals(11, entryCache.getSize());

    // Remove the 'trunk' recursively (trailing slash keeps the dir itself)
    entryCache.removeDirectory("/trunk/");
    assertEquals(4, entryCache.getSize());

    // Remove the 'tags' recursively (without trailing slash everything is deleted)
    entryCache.removeDirectory("/tags");
    assertEquals(1, entryCache.getSize());
  }

  public void testEntryCacheFindPattern() throws Exception {
    assertEquals(0, entryCache.getSize());
    addAll(entryCache, TestUtils.getDirectoryList());
    assertEquals(13, entryCache.getSize());
    assertEquals(1, entryCache.findEntriesByCamelCasePattern(new CamelCasePattern("TDF"), "/").size());
    assertEquals(2, entryCache.findEntriesByCamelCasePattern(new CamelCasePattern("DF"), "/").size());
  }

  private void print(List<RepositoryEntry> entries) {
    for (RepositoryEntry entry : entries) {
      System.out.println(entry);
    }
  }
}
