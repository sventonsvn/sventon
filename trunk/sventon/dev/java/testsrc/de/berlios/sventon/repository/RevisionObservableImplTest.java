package de.berlios.sventon.repository;

import de.berlios.sventon.cache.ObjectCache;
import de.berlios.sventon.cache.ObjectCacheImpl;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;

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

    public Collection log(String[] targetPaths, Collection entries, long startRevision, long endRevision, boolean changedPath, boolean strictNode) throws SVNException {
      List<SVNLogEntry> logs = new ArrayList<SVNLogEntry>();
      logs.add(new SVNLogEntry(new HashMap<String, SVNLogEntryPath>(), 124, "jesper", new Date(), "Log message for revision 124."));
      return logs;
    }

    public long getLatestRevision() throws SVNException {
      return 124;
    }
  }

}