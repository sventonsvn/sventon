package de.berlios.sventon.appl;

import static de.berlios.sventon.TestUtils.TEMPDIR;
import de.berlios.sventon.repository.SVNRepositoryStub;
import de.berlios.sventon.repository.cache.objectcache.ObjectCache;
import de.berlios.sventon.repository.cache.objectcache.ObjectCacheImpl;
import de.berlios.sventon.service.RepositoryServiceImpl;
import de.berlios.sventon.TestUtils;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.*;

import java.io.File;
import java.util.*;

public class RevisionObservableImplTest extends TestCase implements RevisionObserver {

  private ObjectCache createMemoryCache() throws Exception {
    return new ObjectCacheImpl(new RepositoryName("sventonTestCache"), null, 1000, false, false, 0, 0, false, 0);
  }

  public void testUpdate() throws Exception {
    final Application application = TestUtils.getApplicationStub();
    final String name = "defaultsvn";
    final RepositoryConfiguration configuration = new RepositoryConfiguration(name);
    configuration.setCacheUsed(true);
    application.addRepository(configuration);
    application.setConfigured(true);

    final ObjectCache cache = createMemoryCache();

    try {
      final List<RevisionObserver> observers = new ArrayList<RevisionObserver>();
      observers.add(this);
      final RevisionObservableImpl revisionObservable = new RevisionObservableImpl(observers);
      revisionObservable.setMaxRevisionCountPerUpdate(3);
      revisionObservable.setApplication(application);
      revisionObservable.setRepositoryService(new RepositoryServiceImpl());
      assertFalse(application.isUpdating(configuration.getName()));
      revisionObservable.update(configuration.getName(), new TestRepository(), cache, false);
      assertFalse(application.isUpdating(configuration.getName()));
    } finally {
      cache.shutdown();
    }
  }

  @SuppressWarnings({"unchecked"})
  public void update(Observable o, Object arg) {
    //This method will be called twice, with an update containing 3 revisions both times.
    assertEquals(3, ((RevisionUpdate) arg).getRevisions().size());
  }

  public void update(final RevisionUpdate revisionUpdate) {
  }

  static class TestRepository extends SVNRepositoryStub {
    private boolean firstTime = true;

    public TestRepository() throws SVNException {
      super(SVNURL.parseURIDecoded("http://localhost/"), null);
    }

    public long log(String[] targetPaths, long startRevision, long endRevision, boolean changedPath, boolean strictNode, ISVNLogEntryHandler handler) throws SVNException {
      if (firstTime) {
        handler.handleLogEntry(new SVNLogEntry(new HashMap<String, SVNLogEntryPath>(), 1, "jesper1", new Date(), "Log message for revision 1."));
        handler.handleLogEntry(new SVNLogEntry(new HashMap<String, SVNLogEntryPath>(), 2, "jesper2", new Date(), "Log message for revision 2."));
        handler.handleLogEntry(new SVNLogEntry(new HashMap<String, SVNLogEntryPath>(), 3, "jesper3", new Date(), "Log message for revision 3."));
        firstTime = false;
      } else {
        handler.handleLogEntry(new SVNLogEntry(new HashMap<String, SVNLogEntryPath>(), 4, "jesper4", new Date(), "Log message for revision 4."));
        handler.handleLogEntry(new SVNLogEntry(new HashMap<String, SVNLogEntryPath>(), 5, "jesper5", new Date(), "Log message for revision 5."));
        handler.handleLogEntry(new SVNLogEntry(new HashMap<String, SVNLogEntryPath>(), 6, "jesper6", new Date(), "Log message for revision 6."));
      }
      return 1;
    }

    public long getLatestRevision() throws SVNException {
      return 6;
    }
  }

}