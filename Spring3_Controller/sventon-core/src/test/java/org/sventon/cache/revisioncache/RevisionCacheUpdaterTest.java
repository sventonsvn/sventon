package org.sventon.cache.revisioncache;

import org.junit.Test;
import org.sventon.TestUtils;
import org.sventon.cache.objectcache.ObjectCache;
import org.sventon.cache.objectcache.ObjectCacheImpl;
import org.sventon.model.LogEntry;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RevisionCacheUpdaterTest {

  private ObjectCache createMemoryCache() throws Exception {
    return new ObjectCacheImpl("sventonTestCache", null, 1000, false, false, 0, 0, false, 0);
  }

  @Test
  public void testUpdate() throws Exception {
    final ObjectCache objectCache = createMemoryCache();
    final RevisionCacheImpl cache = new RevisionCacheImpl(objectCache);

    try {
      final List<LogEntry> logEntries = new ArrayList<LogEntry>();
      logEntries.add(TestUtils.createLogEntry(123, null, null, "Log message for revision 123."));
      logEntries.add(TestUtils.createLogEntry(124, null, null, "Log message for revision 124."));

      final RevisionCacheUpdater revisionCacheUpdater = new RevisionCacheUpdater(null);
      revisionCacheUpdater.updateInternal(cache, logEntries);
      final LogEntry result1 = cache.get(123);
      final LogEntry result2 = cache.get(124);

      assertEquals(123, result1.getRevision());
      assertEquals(124, result2.getRevision());
    } finally {
      objectCache.shutdown();
    }
  }
}