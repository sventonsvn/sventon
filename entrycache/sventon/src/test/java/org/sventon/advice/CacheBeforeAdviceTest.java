package org.sventon.advice;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.sventon.repository.RevisionObservable;
import org.sventon.model.RepositoryName;

public class CacheBeforeAdviceTest extends TestCase {

  public void testBefore() throws Throwable {
    final CacheUpdateBeforeAdvice cacheUpdateBeforeAdvice = new CacheUpdateBeforeAdvice();
    final RevisionObservable observableMock = EasyMock.createMock(RevisionObservable.class);
    cacheUpdateBeforeAdvice.setRevisionObservable(observableMock);
    final RepositoryName repositoryName = new RepositoryName("repository1");
    observableMock.update(repositoryName, false);
    EasyMock.replay(observableMock);
    cacheUpdateBeforeAdvice.before(null, new Object[]{repositoryName}, null);
    EasyMock.verify(observableMock);
  }
}
