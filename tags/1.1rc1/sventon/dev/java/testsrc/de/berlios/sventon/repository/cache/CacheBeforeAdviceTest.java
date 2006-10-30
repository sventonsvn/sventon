package de.berlios.sventon.repository.cache;

import de.berlios.sventon.repository.RevisionObservable;
import junit.framework.TestCase;

public class CacheBeforeAdviceTest extends TestCase {

  public void testBefore() throws Throwable {
    final CacheBeforeAdvice cacheBeforeAdvice = new CacheBeforeAdvice();

    final TestObservable revisionObservable = new TestObservable();
    cacheBeforeAdvice.setRevisionObservable(revisionObservable);
    cacheBeforeAdvice.before(null, new Object[]{"instance1"}, null);
    assertTrue(revisionObservable.called);
  }

  private static class TestObservable implements RevisionObservable {
    public boolean called = false;

    public void updateAll() {
    }

    public void update(final String instanceName) {
      called = true;
      assertEquals("instance1", instanceName);
    }

    public boolean isUpdating() {
      return false;
    }
  }
}
