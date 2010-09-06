package org.sventon.cache.revisioncache;

import org.junit.Test;
import org.sventon.TestUtils;
import org.sventon.cache.objectcache.ObjectCache;
import org.sventon.cache.objectcache.ObjectCacheImpl;
import org.sventon.model.LogEntry;

import java.util.Date;

import static org.junit.Assert.*;

public class RevisionCacheImplTest {

  private ObjectCache createMemoryCache() throws Exception {
    return new ObjectCacheImpl("sventonTestCache", null, 1000, false, false, 0, 0, false, 0);
  }

  @Test
  public void testGetAndAdd() throws Exception {
    final ObjectCache cache = createMemoryCache();
    final RevisionCacheImpl revisionCache = new RevisionCacheImpl(cache);
    try {
      assertNull(revisionCache.get(123));
      final String author = "TestAuthor";
      final String message = "TestMessage";
      revisionCache.add(TestUtils.createLogEntry(123, author, new Date(), message));
      final LogEntry result = revisionCache.get(123);
      assertNotNull(result);
      assertEquals(123, result.getRevision());
      assertEquals(author, result.getAuthor());
      assertNotNull(result.getDate());
      assertEquals(message, result.getMessage());
    } finally {
      cache.shutdown();
    }
  }

}