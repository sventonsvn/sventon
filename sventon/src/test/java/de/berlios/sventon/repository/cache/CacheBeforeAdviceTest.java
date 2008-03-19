package de.berlios.sventon.repository.cache;

import de.berlios.sventon.appl.RepositoryName;
import de.berlios.sventon.appl.RevisionObservable;
import junit.framework.TestCase;
import org.easymock.EasyMock;

public class CacheBeforeAdviceTest extends TestCase {

  public void testBefore() throws Throwable {
    final CacheBeforeAdvice cacheBeforeAdvice = new CacheBeforeAdvice();
    final RevisionObservable observableMock = EasyMock.createMock(RevisionObservable.class);
    cacheBeforeAdvice.setRevisionObservable(observableMock);
    final RepositoryName repositoryName = new RepositoryName("instance1");
    observableMock.update(repositoryName, false);
    EasyMock.replay(observableMock);
    cacheBeforeAdvice.before(null, new Object[]{repositoryName}, null);
    EasyMock.verify(observableMock);
  }
}
