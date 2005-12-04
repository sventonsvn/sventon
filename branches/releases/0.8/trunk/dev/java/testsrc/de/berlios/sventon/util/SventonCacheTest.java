package de.berlios.sventon.util;

import junit.framework.TestCase;

public class SventonCacheTest extends TestCase {

  public void testPut() throws Exception {
    assertEquals(0, SventonCache.INSTANCE.getMemorySize());
    SventonCache.INSTANCE.put("test1", new Integer(10));
    SventonCache.INSTANCE.put("test2", new Integer(20));
    assertTrue(SventonCache.INSTANCE.getMemorySize() > 100);
    assertEquals(0, SventonCache.INSTANCE.getHitCount());
    assertEquals(new Integer(10), SventonCache.INSTANCE.get("test1"));
    assertEquals(1, SventonCache.INSTANCE.getHitCount());
    assertEquals(new Integer(20), SventonCache.INSTANCE.get("test2"));
    assertEquals(2, SventonCache.INSTANCE.getHitCount());
    try {
      assertNull(SventonCache.INSTANCE.get("test3"));
    } catch (Exception e) {
      fail("Should not cause exception");
    }

  }

  public void testPutNull() throws Exception {
    try {
      SventonCache.INSTANCE.put(null, null);
      fail("Should cause IllegalArgumentException");
    } catch (IllegalArgumentException npe) {
      // expected
    }
  }
}