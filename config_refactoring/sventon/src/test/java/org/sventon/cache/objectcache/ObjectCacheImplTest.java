package org.sventon.cache.objectcache;

import junit.framework.TestCase;

public class ObjectCacheImplTest extends TestCase {

  private ObjectCache createMemoryCache() throws Exception {
    return new ObjectCacheImpl("sventonTestCache", null, 1000, false, false, 0, 0, false, 0);
  }

  public void testPut() throws Exception {
    ObjectCache cache = createMemoryCache();

    cache.put("test1", 10);
    cache.put("test2", 20);

    assertEquals(0, cache.getHitCount());
    assertEquals(0, cache.getMissCount());
    assertEquals(10, cache.get("test1"));
    assertEquals(1, cache.getHitCount());
    assertEquals(0, cache.getMissCount());
    assertEquals(20, cache.get("test2"));
    assertEquals(2, cache.getHitCount());
    assertEquals(0, cache.getMissCount());
    try {
      assertNull(cache.get("test3"));
      assertEquals(1, cache.getMissCount());
    } catch (Exception e) {
      fail("Should not cause exception");
    }
    cache.shutdown();
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