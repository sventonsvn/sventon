package org.sventon.advice;

import org.easymock.EasyMock;
import org.junit.Test;
import org.sventon.model.RepositoryName;
import org.sventon.repository.RepositoryChangeMonitor;

public class CacheBeforeAdviceTest {

  @Test
  public void testBefore() throws Throwable {
    final CacheUpdateBeforeAdvice cacheUpdateBeforeAdvice = new CacheUpdateBeforeAdvice();
    final RepositoryChangeMonitor changeMonitorMock = EasyMock.createMock(RepositoryChangeMonitor.class);
    cacheUpdateBeforeAdvice.setRepositoryChangeMonitor(changeMonitorMock);
    final RepositoryName repositoryName = new RepositoryName("repository1");
    changeMonitorMock.update(repositoryName);
    EasyMock.replay(changeMonitorMock);
    cacheUpdateBeforeAdvice.before(null, new Object[]{repositoryName}, null);
    EasyMock.verify(changeMonitorMock);
  }
}
