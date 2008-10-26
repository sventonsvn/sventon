package org.sventon.advice;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.sventon.appl.RevisionObservable;
import org.sventon.model.RepositoryName;

public class CacheBeforeAdviceTest extends TestCase {

  public void testBefore() throws Throwable {
    final CacheBeforeAdvice cacheBeforeAdvice = new CacheBeforeAdvice();
    final RevisionObservable observableMock = EasyMock.createMock(RevisionObservable.class);
    cacheBeforeAdvice.setRevisionObservable(observableMock);
    final RepositoryName repositoryName = new RepositoryName("repository1");
    observableMock.update(repositoryName, false);
    EasyMock.replay(observableMock);
    cacheBeforeAdvice.before(null, new Object[]{repositoryName}, null);
    EasyMock.verify(observableMock);
  }
}
