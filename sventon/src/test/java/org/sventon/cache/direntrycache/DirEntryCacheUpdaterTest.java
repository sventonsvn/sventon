package org.sventon.cache.direntrycache;

import junit.framework.TestCase;
import org.springframework.mock.web.MockServletContext;
import org.sventon.SVNRepositoryStub;
import org.sventon.TestUtils;
import org.sventon.appl.Application;
import org.sventon.appl.ConfigDirectory;
import org.sventon.model.RepositoryName;
import org.sventon.repository.RevisionUpdate;
import org.sventon.service.svnkit.SVNKitConnection;
import org.sventon.service.svnkit.SVNKitRepositoryService;
import org.tmatesoft.svn.core.*;

import java.io.File;
import java.util.*;

public class DirEntryCacheUpdaterTest extends TestCase {

  public void testUpdate() throws Exception {
    final DirEntryCache entryCache = new CompassDirEntryCache(new File("test"));
    entryCache.init();
    assertEquals(0, entryCache.getSize());

    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    final Map<String, SVNLogEntryPath> changedPaths1 = new HashMap<String, SVNLogEntryPath>();
    changedPaths1.put("/file1.java", new SVNLogEntryPath("/file1.java", 'M', null, -1));
    changedPaths1.put("/file2.abc", new SVNLogEntryPath("/file2.abc", 'A', null, -1));
    changedPaths1.put("/trunk/file3.def", new SVNLogEntryPath("/trunk/file3.def", 'R', null, -1));
    logEntries.add(new SVNLogEntry(changedPaths1, 123, "author", new Date(), "Log message for revision 123."));

    final Map<String, SVNLogEntryPath> changedPaths2 = new HashMap<String, SVNLogEntryPath>();
    changedPaths2.put("/branch", new SVNLogEntryPath("/branch", 'A', "/trunk", 123));
    changedPaths2.put("/branch/file3.def", new SVNLogEntryPath("/branch/file3.def", 'D', null, -1));
    logEntries.add(new SVNLogEntry(changedPaths2, 124, "author", new Date(), "Log message for revision 124."));

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

    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    logEntries.add(new SVNLogEntry(Collections.EMPTY_MAP, 1, "author", new Date(), "Log message for revision 1."));

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
