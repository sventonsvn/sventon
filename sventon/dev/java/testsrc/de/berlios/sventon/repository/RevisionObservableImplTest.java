package de.berlios.sventon.repository;

import de.berlios.sventon.cache.ObjectCache;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import java.util.*;

public class RevisionObservableImplTest extends TestCase implements RevisionObserver {

  public void testUpdate() throws Exception {
    final ObjectCache cache = new MemoryCache();

    final RepositoryConfiguration configuration = new RepositoryConfiguration();
    configuration.setCacheUsed(true);

    final List<RevisionObserver> observers = new ArrayList<RevisionObserver>();
    observers.add(this);
    final RevisionObservableImpl revisionObservableImpl = new RevisionObservableImpl(observers);
    revisionObservableImpl.setRepository(new TestRepository());
    revisionObservableImpl.setRepositoryConfiguration(configuration);
    revisionObservableImpl.setObjectCache(cache);
    assertFalse(revisionObservableImpl.isUpdating());
    revisionObservableImpl.update();
  }

  public void update(Observable o, Object arg) {
    assertEquals(1, ((List<SVNLogEntry>)arg).size());
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

  class MemoryCache implements ObjectCache {
    private Map cache = new HashMap();

    public void put(final Object cacheKey, final Object value) {
      cache.put(cacheKey, value);
    }

    public Object get(final Object cacheKey) {
      return cache.get(cacheKey);
    }

    public long getHitCount() {
      return 0;
    }

    public long getMissCount() {
      return 0;
    }

    public void shutdown() throws Exception {
    }
  }
}