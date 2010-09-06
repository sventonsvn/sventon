package org.sventon.cache.objectcache;

import org.junit.Test;

import static org.junit.Assert.fail;

public class ObjectCacheImplTest {

  private ObjectCache createMemoryCache() throws Exception {
    return new ObjectCacheImpl("sventonTestCache", null, 1000, false, false, 0, 0, false, 0);
  }

  @Test
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

  @Test
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