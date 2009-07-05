package org.sventon.cache.objectcache;

import junit.framework.TestCase;
import net.sf.ehcache.CacheManager;

public class ObjectCacheImplTest extends TestCase {

  private ObjectCache createMemoryCache() throws Exception {
    final CacheManager cacheManager = CacheManager.create();
    return new ObjectCacheImpl(cacheManager, "sventonTestCache", null, 1000, false, false, 0, 0, false, 0);
  }

  public void testPutNull() throws Exception {
    ObjectCache cache = createMemoryCache();
    try {
      cache.put(null, null);
      fail("Should cause IllegalArgumentException");
    } catch (IllegalArgumentException npe) {
      // expected
    }
    cache.shutdown();
  }

  public void testGetNull() throws Exception {
    ObjectCache cache = createMemoryCache();
    try {
      cache.get(null);
      fail("Should cause IllegalArgumentException");
    } catch (IllegalArgumentException npe) {
      // expected
    }
    cache.shutdown();
  }

}