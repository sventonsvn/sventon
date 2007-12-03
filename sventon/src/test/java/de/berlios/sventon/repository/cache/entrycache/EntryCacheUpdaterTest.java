package de.berlios.sventon.repository.cache.entrycache;

import de.berlios.sventon.appl.Application;
import de.berlios.sventon.appl.RevisionUpdate;
import de.berlios.sventon.repository.SVNRepositoryStub;
import de.berlios.sventon.service.RepositoryServiceImpl;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.*;

import java.io.File;
import java.util.*;

public class EntryCacheUpdaterTest extends TestCase {

  private static final String TEMPDIR = System.getProperty("java.io.tmpdir");

  public void testUpdate() throws Exception {
    final EntryCache entryCache = new MemoryCache();
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

    final Application application = new Application(new File(TEMPDIR), "filename");
    application.setRepositoryService(new RepositoryServiceImpl());
    new EntryCacheUpdater(null, application).updateInternal(entryCache, new TestRepository(),
        new RevisionUpdate("defaultsvn", logEntries, false, false));

    assertEquals(4, entryCache.getSize());
  }


  private static class TestRepository extends SVNRepositoryStub {
    private final Map<String, SVNDirEntry> entriesMap = new HashMap<String, SVNDirEntry>();

    public TestRepository() throws SVNException {
      super(SVNURL.parseURIDecoded("http://localhost/"), null);
      entriesMap.put("/file1.java#123", new SVNDirEntry(SVNURL.parseURIDecoded("http://localhost/repo/file1.java"), "file1.java", SVNNodeKind.FILE, 12345, false, 123, new Date(), "author"));
      entriesMap.put("/file2.abc#123", new SVNDirEntry(SVNURL.parseURIDecoded("http://localhost/repo/file2.abc"), "file2.abc", SVNNodeKind.FILE, 12345, false, 123, new Date(), "author"));
      entriesMap.put("/trunk#123", new SVNDirEntry(SVNURL.parseURIDecoded("http://localhost/repo/trunk"), "trunk", SVNNodeKind.FILE, 12345, false, 123, new Date(), "author"));
      entriesMap.put("/trunk/file3.def#123", new SVNDirEntry(SVNURL.parseURIDecoded("http://localhost/repo/trunk/file3.def"), "file3.def", SVNNodeKind.FILE, 12345, false, 123, new Date(), "author"));
      entriesMap.put("/branch#124", new SVNDirEntry(SVNURL.parseURIDecoded("http://localhost/repo/branch"), "branch", SVNNodeKind.FILE, 12345, false, 124, new Date(), "author"));
    }

    public SVNDirEntry info(final String path, final long revision) throws SVNException {
      return entriesMap.get(path + "#" + revision);
    }
  }
}
