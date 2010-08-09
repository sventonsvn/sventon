package org.sventon.repository;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import org.springframework.mock.web.MockServletContext;
import org.sventon.TestUtils;
import org.sventon.appl.Application;
import org.sventon.appl.ConfigDirectory;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.cache.objectcache.ObjectCache;
import org.sventon.cache.objectcache.ObjectCacheImpl;
import org.sventon.service.RepositoryService;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RepositoryChangeMonitorImplTest extends TestCase {

  private RepositoryService repositoryServiceMock = createMock(RepositoryService.class);
  private RepositoryChangeListener repositoryChangeListenerMock = createMock(RepositoryChangeListener.class);
  private List<SVNLogEntry> firstBatchOfRevisions;
  private List<SVNLogEntry> secondBatchOfRevisions;

  @Override
  protected void setUp() throws Exception {
    firstBatchOfRevisions = new ArrayList<SVNLogEntry>();
    secondBatchOfRevisions = new ArrayList<SVNLogEntry>();

    firstBatchOfRevisions.add(new SVNLogEntry(new HashMap<String, SVNLogEntryPath>(), 1, "jesper1", new Date(), "Log message for revision 1."));
    firstBatchOfRevisions.add(new SVNLogEntry(new HashMap<String, SVNLogEntryPath>(), 2, "jesper2", new Date(), "Log message for revision 2."));
    firstBatchOfRevisions.add(new SVNLogEntry(new HashMap<String, SVNLogEntryPath>(), 3, "jesper3", new Date(), "Log message for revision 3."));

    secondBatchOfRevisions.add(new SVNLogEntry(new HashMap<String, SVNLogEntryPath>(), 4, "jesper4", new Date(), "Log message for revision 4."));
    secondBatchOfRevisions.add(new SVNLogEntry(new HashMap<String, SVNLogEntryPath>(), 5, "jesper5", new Date(), "Log message for revision 5."));
    secondBatchOfRevisions.add(new SVNLogEntry(new HashMap<String, SVNLogEntryPath>(), 6, "jesper6", new Date(), "Log message for revision 6."));
  }

  @Override
  protected void tearDown() throws Exception {
    reset(repositoryServiceMock);
    reset(repositoryChangeListenerMock);
  }

  private ObjectCache createMemoryCache() throws Exception {
    return new ObjectCacheImpl("sventonTestCache", null, 1000, false, false, 0, 0, false, 0);
  }

  public void testUpdate() throws Exception {
    final ConfigDirectory configDirectory = TestUtils.getTestConfigDirectory();
    configDirectory.setCreateDirectories(false);
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);

    final Application application = new Application(configDirectory, TestUtils.CONFIG_FILE_NAME);

    final RepositoryConfiguration configuration = new RepositoryConfiguration("name");
    configuration.setCacheUsed(true);
    application.addRepository(configuration);
    application.setConfigured(true);

    final ObjectCache cache = createMemoryCache();

    try {
      final List<RepositoryChangeListener> listeners = new ArrayList<RepositoryChangeListener>();
      listeners.add(repositoryChangeListenerMock);
      final RepositoryChangeMonitorImpl changeMonitor = new RepositoryChangeMonitorImpl(listeners);
      changeMonitor.setMaxRevisionCountPerUpdate(3);
      changeMonitor.setApplication(application);

      changeMonitor.setRepositoryService(repositoryServiceMock);
      assertFalse(application.isUpdating(configuration.getName()));

      expect(repositoryServiceMock.getLatestRevision(null)).andReturn(6L);
      expect(repositoryServiceMock.getLogFromRepository(null, 1L, 3L)).andReturn(firstBatchOfRevisions);
      repositoryChangeListenerMock.update(isA(RevisionUpdate.class));
      expect(repositoryServiceMock.getLogFromRepository(null, 4L, 6L)).andReturn(secondBatchOfRevisions);
      repositoryChangeListenerMock.update(isA(RevisionUpdate.class));

      replay(repositoryServiceMock);
      replay(repositoryChangeListenerMock);
      changeMonitor.update(configuration.getName(), null, cache, false);
      verify(repositoryServiceMock);
      verify(repositoryChangeListenerMock);

      assertFalse(application.isUpdating(configuration.getName()));
    } finally {
      cache.shutdown();
    }
  }
}