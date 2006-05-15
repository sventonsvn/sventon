package de.berlios.sventon.cache;

import junit.framework.TestCase;

public class ObjectCacheImplTest extends TestCase {

  public void testPut() throws Exception {
    ObjectCache cache = new ObjectCacheImpl();
    cache.put("test1", new Integer(10));
    cache.put("test2", new Integer(20));

    assertEquals(0, cache.getHitCount());
    assertEquals(0, cache.getMissCount());
    assertEquals(new Integer(10), cache.get("test1"));
    assertEquals(1, cache.getHitCount());
    assertEquals(0, cache.getMissCount());
    assertEquals(new Integer(20), cache.get("test2"));
    assertEquals(2, cache.getHitCount());
    assertEquals(0, cache.getMissCount());
    try {
      assertNull(cache.get("test3"));
      assertEquals(1, cache.getMissCount());
    } catch (Exception e) {
      fail("Should not cause exception");
    }
  }

  public void testPutNull() throws Exception {
    ObjectCache cache = new ObjectCacheImpl();
    try {
      cache.put(null, null);
      fail("Should cause IllegalArgumentException");
    } catch (IllegalArgumentException npe) {
      // expected
    }
  }

  public void testGetNull() throws Exception {
    ObjectCache cache = new ObjectCacheImpl();
    try {
      cache.get(null);
      fail("Should cause IllegalArgumentException");
    } catch (IllegalArgumentException npe) {
      // expected
    }
  }

}