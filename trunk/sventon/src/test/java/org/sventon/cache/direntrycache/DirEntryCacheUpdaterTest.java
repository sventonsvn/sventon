package org.sventon.cache.direntrycache;

import junit.framework.TestCase;
import org.springframework.mock.web.MockServletContext;
import org.sventon.SVNRepositoryStub;
import org.sventon.TestUtils;
import org.sventon.appl.Application;
import org.sventon.appl.ConfigDirectory;
import org.sventon.model.ChangeType;
import org.sventon.model.ChangedPath;
import org.sventon.model.LogEntry;
import org.sventon.model.RepositoryName;
import org.sventon.repository.RevisionUpdate;
import org.sventon.service.svnkit.SVNKitConnection;
import org.sventon.service.svnkit.SVNKitRepositoryService;
import org.tmatesoft.svn.core.*;

import java.io.File;
import java.util.*;

import static org.sventon.TestUtils.createLogEntry;

public class DirEntryCacheUpdaterTest extends TestCase {

  public void testUpdate() throws Exception {
    final DirEntryCache entryCache = new CompassDirEntryCache(new File("test"));
    entryCache.init();
    assertEquals(0, entryCache.getSize());

    final List<LogEntry> logEntries = new ArrayList<LogEntry>();
    final Set<ChangedPath> changedPaths1 = new TreeSet<ChangedPath>();
    changedPaths1.add(new ChangedPath("/file1.java", null, -1, ChangeType.MODIFIED));
    changedPaths1.add(new ChangedPath("/file2.abc", null, -1, ChangeType.ADDED));
    changedPaths1.add(new ChangedPath("/trunk/file3.def", null, -1, ChangeType.REPLACED));
    logEntries.add(createLogEntry(123, "author", new Date(), "Log message for revision 123.", changedPaths1));

    final Set<ChangedPath> changedPaths2 = new TreeSet<ChangedPath>();
    changedPaths2.add(new ChangedPath("/branch", "/trunk", 123, ChangeType.ADDED));
    changedPaths2.add(new ChangedPath("/branch/file3.def", null, -1, ChangeType.DELETED));
    logEntries.add(createLogEntry(124, "author", new Date(), "Log message for revision 124.", changedPaths2));

    final ConfigDirectory configDirectory = TestUtils.getTestConfigDirectory();
    configDirectory.setCreateDirectories(false);
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);
    final Application application = new Application(configDirectory);

    final DirEntryCacheUpdater cacheUpdater = new DirEntryCacheUpdater(null, application);
    cacheUpdater.setRepositoryService(new SVNKitRepositoryService());
    cacheUpdater.updateInternal(entryCache, new SVNKitConnection(new TestRepository()),
        new RevisionUpdate(new RepositoryName("defaultsvn"), logEntries, false, false));

    assertEquals(4, entryCache.getSize());
  }

  public void testInitialUpdate() throws Exception {
    final DirEntryCache entryCache = new CompassDirEntryCache(new File("test"));
    entryCache.init();
    assertEquals(0, entryCache.getSize());

    final List<LogEntry> logEntries = new ArrayList<LogEntry>();
    logEntries.add(createLogEntry(1, "author", new Date(), "Log message for revision 1.", null));

    final ConfigDirectory configDirectory = TestUtils.getTestConfigDirectory();
    configDirectory.setCreateDirectories(false);
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);
    final Application application = new Application(configDirectory);

    final DirEntryCacheUpdater cacheUpdater = new DirEntryCacheUpdater(null, application);
    cacheUpdater.setFlushThreshold(2);
    cacheUpdater.setRepositoryService(new SVNKitRepositoryService());
    cacheUpdater.updateInternal(entryCache, new SVNKitConnection(new TestRepository()),
        new RevisionUpdate(new RepositoryName("defaultsvn"), logEntries, false, false));

    assertEquals(3, entryCache.getSize());
  }

  private static class TestRepository extends SVNRepositoryStub {
    private final Map<String, SVNDirEntry> entriesMap = new HashMap<String, SVNDirEntry>();

    public TestRepository() throws SVNException {
      entriesMap.put("/file1.java@123", new SVNDirEntry(SVNURL.parseURIDecoded("http://localhost/repo/file1.java"), null, "file1.java", SVNNodeKind.FILE, 12345, false, 123, new Date(), "author"));
      entriesMap.put("/file2.abc@123", new SVNDirEntry(SVNURL.parseURIDecoded("http://localhost/repo/file2.abc"), null, "file2.abc", SVNNodeKind.FILE, 12345, false, 123, new Date(), "author"));
      entriesMap.put("/trunk@123", new SVNDirEntry(SVNURL.parseURIDecoded("http://localhost/repo/trunk"), null, "trunk", SVNNodeKind.FILE, 12345, false, 123, new Date(), "author"));
      entriesMap.put("/trunk/file3.def@123", new SVNDirEntry(SVNURL.parseURIDecoded("http://localhost/repo/trunk/file3.def"), null, "file3.def", SVNNodeKind.FILE, 12345, false, 123, new Date(), "author"));
      entriesMap.put("/branch@124", new SVNDirEntry(SVNURL.parseURIDecoded("http://localhost/repo/branch"), null, "branch", SVNNodeKind.FILE, 12345, false, 124, new Date(), "author"));
    }

    @Override
    public SVNDirEntry info(final String path, final long revision) throws SVNException {
      return entriesMap.get(path + "@" + revision);
    }

    @Override
    public long getLatestRevision() throws SVNException {
      return 2;
    }

    @Override
    public Collection<SVNDirEntry> getDir(String path, long revision, SVNProperties properties, Collection collection) throws SVNException {
      final List<SVNDirEntry> entries = new ArrayList<SVNDirEntry>();
      if ("/".equals(path)) {
        entries.add(new SVNDirEntry(null, null, "branches", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"));
        entries.add(new SVNDirEntry(null, null, "trunk", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"));
        entries.add(new SVNDirEntry(null, null, "tags", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"));
      }
      return entries;
    }
  }
}
