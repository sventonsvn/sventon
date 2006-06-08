package de.berlios.sventon.repository;

import de.berlios.sventon.cache.ObjectCache;
import de.berlios.sventon.cache.ObjectCacheImpl;
import de.berlios.sventon.service.RepositoryServiceImpl;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.*;

import java.util.*;

public class RevisionObservableImplTest extends TestCase implements RevisionObserver {

  private ObjectCache createMemoryCache() throws Exception {
    return new ObjectCacheImpl("sventonTestCache", 1000, false, false, 0, 0, false, 0);
  }

  public void testUpdate() throws Exception {
    final RepositoryConfiguration configuration = new RepositoryConfiguration();
    configuration.setCacheUsed(true);

    final ObjectCache cache = createMemoryCache();

    try {
      final List<RevisionObserver> observers = new ArrayList<RevisionObserver>();
      observers.add(this);
      final RevisionObservableImpl revisionObservable = new RevisionObservableImpl(observers);
      revisionObservable.setRepository(new TestRepository());
      revisionObservable.setRepositoryConfiguration(configuration);
      revisionObservable.setObjectCache(cache);
      revisionObservable.setRepositoryService(new RepositoryServiceImpl());
      assertFalse(revisionObservable.isUpdating());
      revisionObservable.update();
    } finally {
      cache.shutdown();
    }
  }

  public void update(Observable o, Object arg) {
    assertEquals(1, ((List<SVNLogEntry>) arg).size());
  }

  public void update(final List<SVNLogEntry> revisions) {
  }

  class TestRepository extends SVNRepositoryStub {
    public TestRepository() throws SVNException {
      super(SVNURL.parseURIDecoded("http://localhost/"), null);
    }

    public long log(String[] targetPaths, long startRevision, long endRevision, boolean changedPath, boolean strictNode, long limit, ISVNLogEntryHandler handler) throws SVNException {
      handler.handleLogEntry(new SVNLogEntry(new HashMap<String, SVNLogEntryPath>(), 124, "jesper", new Date(), "Log message for revision 124."));
      return 1;
    }

    public long getLatestRevision() throws SVNException {
      return 124;
    }
  }

}