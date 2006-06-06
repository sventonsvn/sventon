package de.berlios.sventon.repository.cache.revisioncache;


import de.berlios.sventon.cache.ObjectCache;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.Date;
import java.util.HashMap;

public class RevisionCacheImplTest extends TestCase {

//  private ObjectCache createMemoryCache() throws Exception {
//    return new ObjectCacheImpl("sventonTestCache", 1000, false, false, 0, 0, false, 0);
//  }

  public void testGetAndAdd() throws Exception {
    final RevisionCacheImpl revisionCache = new RevisionCacheImpl();
    revisionCache.setObjectCache(new TestObjectCache());

    assertNull(revisionCache.get(1));

    final SVNLogEntry logEntry = new SVNLogEntry(null, 1, "author", new Date(), "a log message");
    revisionCache.add(logEntry);

    SVNLogEntry result = revisionCache.get(1);
    assertNotNull(result);
    assertEquals(1, result.getRevision());
    assertEquals("author", result.getAuthor());
    assertNotNull(result.getDate());
    assertEquals("a log message", result.getMessage());
  }

  class TestObjectCache implements ObjectCache {
    private HashMap cache = new HashMap();

    public void put(final Object cacheKey, final Object value) {
      cache.put(cacheKey, value);
    }

    public Object get(final Object cacheKey) {
      return cache.get(cacheKey);
    }

    public long getHitCount() {
      return 0;
    }

    public long getMissCount() {
      return 0;
    }

    public void shutdown() throws Exception {
    }
  }

}