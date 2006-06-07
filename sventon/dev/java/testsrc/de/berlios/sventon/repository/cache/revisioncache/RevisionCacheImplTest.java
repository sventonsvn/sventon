package de.berlios.sventon.repository.cache.revisioncache;


import de.berlios.sventon.cache.ObjectCache;
import de.berlios.sventon.cache.ObjectCacheImpl;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.Date;

public class RevisionCacheImplTest extends TestCase {

  private ObjectCache createMemoryCache() throws Exception {
    return new ObjectCacheImpl("sventonTestCache", 1000, false, false, 0, 0, false, 0);
  }

  public void testGetAndAdd() throws Exception {
    final ObjectCache cache = createMemoryCache();
    final RevisionCacheImpl revisionCache = new RevisionCacheImpl();
    revisionCache.setObjectCache(cache);
    try {
      assertNull(revisionCache.get(1));

      final SVNLogEntry logEntry = new SVNLogEntry(null, 1, "author", new Date(), "a log message");
      revisionCache.add(logEntry);

      SVNLogEntry result = revisionCache.get(1);
      assertNotNull(result);
      assertEquals(1, result.getRevision());
      assertEquals("author", result.getAuthor());
      assertNotNull(result.getDate());
      assertEquals("a log message", result.getMessage());
    } finally {
      cache.shutdown();
    }
  }

}