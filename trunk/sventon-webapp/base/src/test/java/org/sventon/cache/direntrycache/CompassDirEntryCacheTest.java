/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.cache.direntrycache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sventon.TestUtils;
import org.sventon.model.CamelCasePattern;
import org.sventon.model.DirEntry;

import java.io.File;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class CompassDirEntryCacheTest {

  private final File root = new File(".");

  private DirEntryCache entryCache;

  @Before
  public void setUp() throws Exception {
    entryCache = new CompassDirEntryCache(root, false);
    entryCache.init();
  }

  @After
  public void tearDown() throws Exception {
    entryCache.shutdown();
  }

  @Test
  public void testEntryCache() throws Exception {
    assertEquals(0, entryCache.getLatestCachedRevisionNumber());
    assertEquals(0, entryCache.getSize());
  }

  @Test
  public void testEntryCacheClear() throws Exception {
    assertEquals(0, entryCache.getSize());
    addAll(entryCache, TestUtils.getDirectoryList());
    assertEquals(13, entryCache.getSize());
    entryCache.clear();
    assertEquals(0, entryCache.getSize());
  }

  @Test
  public void testEntryCacheAdd() throws Exception {
    assertEquals(0, entryCache.getSize());
    addAll(entryCache, TestUtils.getDirectoryList());
    assertEquals(13, entryCache.getSize());
  }

  @Test
  public void testFindEntriesUsingIllegalCharacters() throws Exception {
    assertTrue(entryCache.findEntries("-", "/").isEmpty());
    assertTrue(entryCache.findEntries("_", "/").isEmpty());
    entryCache.add(new DirEntry("/", "test_file.java", "jesper", new Date(), DirEntry.Kind.FILE, 1, 64000));
    entryCache.add(new DirEntry("/", "test-file.java", "jesper", new Date(), DirEntry.Kind.FILE, 1, 64000));
    entryCache.add(new DirEntry("/", "simple-1.0-rc1", "jesper", new Date(), DirEntry.Kind.FILE, 1, 64000));
    entryCache.add(new DirEntry("/", "WEB-INF", "jesper", new Date(), DirEntry.Kind.DIR, 1, 0));
    assertFalse(entryCache.findEntries("test_file", "/").isEmpty());
    assertFalse(entryCache.findEntries("test-file", "/").isEmpty());
    assertFalse(entryCache.findEntries("simple-1.0-rc1", "/").isEmpty());
    assertTrue(entryCache.findEntries("simple-1.0", "/").isEmpty());
    assertFalse(entryCache.findEntries("WEB-INF", "/").isEmpty());
  }

  @Test
  public void testFindEntry() throws Exception {
    addAll(entryCache, TestUtils.getDirectoryList());
    assertEquals(2, entryCache.findEntries("trunk", "/").size());
    assertEquals(2, entryCache.findEntries("truNK", "/").size());
    assertEquals(1, entryCache.findEntries("TAGS", "/").size());
    assertEquals(5, entryCache.findEntries("java", "/").size());
    assertEquals(1, entryCache.findEntries("java", "/trunk/code/").size());
    assertEquals(2, entryCache.findEntries("html", "/").size());
    assertEquals(2, entryCache.findEntries("test", "/").size());
    assertEquals(13, entryCache.findEntries("in", "/").size());
    assertEquals(7, entryCache.findEntries("*", "/trunk").size());
    assertEquals(2, entryCache.findEntries("*", "/TAGS").size());
    assertEquals(7, entryCache.findEntries("File", "/").size());
    assertEquals(7, entryCache.findEntries("file", "/").size());
    assertEquals(5, entryCache.findEntries("file", "/trunk/").size());
    assertEquals(3, entryCache.findEntries("dir", "").size());
  }

  @Test
  public void testFindEntryByAuthor() throws Exception {
    assertEquals(0, entryCache.getSize());
    addAll(entryCache, TestUtils.getDirectoryList());
    assertEquals(13, entryCache.getSize());

    assertEquals(11, entryCache.findEntries("jesper", "/").size());
    assertEquals(2, entryCache.findEntries("jesper", "/TAGS/").size());
  }

  @Test
  public void testEntryCacheRemove() throws Exception {
    assertEquals(0, entryCache.getSize());
    addAll(entryCache, TestUtils.getDirectoryList());
    assertEquals(13, entryCache.getSize());

    entryCache.removeFile("/file1.java");
    assertEquals(12, entryCache.getSize());

    // Try to remove again
    entryCache.removeFile("/file1.java");
    assertEquals(12, entryCache.getSize());

    // Must not matter in this case (entry is a file)
    entryCache.removeDirectory("/file2.html");
    assertEquals(11, entryCache.getSize());

    // Remove the 'trunk' recursively (trailing slash keeps the dir itself)
    entryCache.removeDirectory("/trunk/");
    assertEquals(4, entryCache.getSize());

    // Remove the 'tags' recursively (without trailing slash everything is deleted)
    entryCache.removeDirectory("/TAGS");
    assertEquals(1, entryCache.getSize());
  }

  @Test
  public void testEntryCacheFindPattern() throws Exception {
    assertEquals(0, entryCache.getSize());
    addAll(entryCache, TestUtils.getDirectoryList());
    assertEquals(13, entryCache.getSize());
    assertEquals(1, entryCache.findEntriesByCamelCasePattern(new CamelCasePattern("TDF"), "/").size());
    assertEquals(2, entryCache.findEntriesByCamelCasePattern(new CamelCasePattern("DF"), "/").size());
  }

  private void print(List<DirEntry> entries) {
    for (DirEntry entry : entries) {
      System.out.println(entry);
    }
  }

  private void addAll(DirEntryCache entryCache, List<DirEntry> entries) {
    entryCache.add(entries.toArray(new DirEntry[entries.size()]));
  }

}
