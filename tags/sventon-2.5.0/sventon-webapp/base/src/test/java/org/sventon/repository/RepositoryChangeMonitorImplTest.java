/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.sventon.TestUtils;
import org.sventon.appl.Application;
import org.sventon.appl.ConfigDirectory;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.cache.objectcache.ObjectCache;
import org.sventon.cache.objectcache.ObjectCacheImpl;
import org.sventon.model.LogEntry;
import org.sventon.service.RepositoryService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertFalse;

public class RepositoryChangeMonitorImplTest {

  private RepositoryService repositoryServiceMock = createMock(RepositoryService.class);
  private RepositoryChangeListener repositoryChangeListenerMock = createMock(RepositoryChangeListener.class);
  private List<LogEntry> firstBatchOfRevisions;
  private List<LogEntry> secondBatchOfRevisions;

  @Before
  public void setUp() throws Exception {
    firstBatchOfRevisions = new ArrayList<LogEntry>();
    secondBatchOfRevisions = new ArrayList<LogEntry>();

    firstBatchOfRevisions.add(TestUtils.createLogEntry(1, "jesper1", new Date(), "Log message for revision 1."));
    firstBatchOfRevisions.add(TestUtils.createLogEntry(2, "jesper2", new Date(), "Log message for revision 2."));
    firstBatchOfRevisions.add(TestUtils.createLogEntry(3, "jesper3", new Date(), "Log message for revision 3."));

    secondBatchOfRevisions.add(TestUtils.createLogEntry(4, "jesper4", new Date(), "Log message for revision 4."));
    secondBatchOfRevisions.add(TestUtils.createLogEntry(5, "jesper5", new Date(), "Log message for revision 5."));
    secondBatchOfRevisions.add(TestUtils.createLogEntry(6, "jesper6", new Date(), "Log message for revision 6."));
  }

  @After
  public void tearDown() throws Exception {
    reset(repositoryServiceMock);
    reset(repositoryChangeListenerMock);
  }

  private ObjectCache createMemoryCache() throws Exception {
    return new ObjectCacheImpl("sventonTestCache", null, 1000, false, false, 0, 0, false, 0);
  }

  @Test
  public void testUpdate() throws Exception {
    final ConfigDirectory configDirectory = TestUtils.getTestConfigDirectory();
    configDirectory.setCreateDirectories(false);
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);

    final Application application = new Application(configDirectory);

    final RepositoryConfiguration configuration = new RepositoryConfiguration("name");
    configuration.setCacheUsed(true);
    application.addConfiguration(configuration);
    application.setConfigured(true);

    final ObjectCache cache = createMemoryCache();

    try {
      final List<RepositoryChangeListener> listeners = new ArrayList<RepositoryChangeListener>();
      listeners.add(repositoryChangeListenerMock);
      final RepositoryChangeMonitorImpl changeMonitor = new RepositoryChangeMonitorImpl();
      changeMonitor.setListeners(listeners);
      changeMonitor.setMaxRevisionCountPerUpdate(3);
      changeMonitor.setApplication(application);

      changeMonitor.setRepositoryService(repositoryServiceMock);
      assertFalse(application.isUpdating(configuration.getName()));

      expect(repositoryServiceMock.getLatestRevision(null)).andReturn(6L);
      expect(repositoryServiceMock.getLogEntriesFromRepositoryRoot(null, 1L, 3L)).andReturn(firstBatchOfRevisions);
      repositoryChangeListenerMock.update(isA(RevisionUpdate.class));
      expect(repositoryServiceMock.getLogEntriesFromRepositoryRoot(null, 4L, 6L)).andReturn(secondBatchOfRevisions);
      repositoryChangeListenerMock.update(isA(RevisionUpdate.class));

      replay(repositoryServiceMock);
      replay(repositoryChangeListenerMock);
      changeMonitor.update(configuration.getName(), null, cache);
      verify(repositoryServiceMock);
      verify(repositoryChangeListenerMock);

      assertFalse(application.isUpdating(configuration.getName()));
    } finally {
      cache.shutdown();
    }
  }
}