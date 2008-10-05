package org.sventon.cache;

import junit.framework.TestCase;
import org.sventon.SVNRepositoryStub;
import org.sventon.TestUtils;
import org.sventon.cache.entrycache.EntryCache;
import org.sventon.cache.entrycache.EntryCacheManager;
import org.sventon.cache.entrycache.MemoryCache;
import org.sventon.model.RepositoryName;
import org.tmatesoft.svn.core.*;

import java.io.File;
import java.util.*;

public class CacheGatewayImplTest extends TestCase {

  private final RepositoryName repositoryName = new RepositoryName("testRepos");

  private CacheGateway createCache() throws CacheException {
    final EntryCacheManager cacheManager = new EntryCacheManager(new File("/"));
    final EntryCache entryCache = new MemoryCache();
    entryCache.init();
    cacheManager.addCache(repositoryName, entryCache);
    entryCache.add(TestUtils.getDirectoryList());
    final CacheGatewayImpl cache = new CacheGatewayImpl();
    cache.setEntryCacheManager(cacheManager);
    return cache;
  }

  public void testFindEntryInPath() throws Exception {
    final CacheGateway cache = createCache();
    assertEquals(1, cache.findEntry(repositoryName, "html", "/trunk/src/").size());

    assertEquals(5, cache.findEntry(repositoryName, "java", "/").size());
    assertEquals(1, cache.findEntry(repositoryName, "code", "/").size());
  }

  public void testFindEntryByCamelCase() throws Exception {
    final CacheGateway cache = createCache();
    final CamelCasePattern ccPattern = new CamelCasePattern("DF");
    assertEquals(2, cache.findEntryByCamelCase(repositoryName, ccPattern, "/trunk/").size());
  }

  public void testFindDirectories() throws Exception {
    final CacheGateway cache = createCache();
    assertEquals(4, cache.findDirectories(repositoryName, "/").size());
    assertEquals(2, cache.findDirectories(repositoryName, "/trunk/").size());
  }

  static class TestRepository extends SVNRepositoryStub {
    private HashMap<String, Collection> repositoryEntries = new HashMap<String, Collection>();
    private List<SVNLogEntry> logEntriesForPopulation = new ArrayList<SVNLogEntry>();
    private List<SVNLogEntry> logEntriesForUpdate = new ArrayList<SVNLogEntry>();
    private SVNDirEntry infoEntry;
    private long headRevision = 123;

    public TestRepository() throws SVNException {
      super(SVNURL.parseURIDecoded("http://localhost/"), null);

      final List<SVNDirEntry> entries1 = new ArrayList<SVNDirEntry>();
      entries1.add(new SVNDirEntry(null, null, "file1.java", SVNNodeKind.FILE, 64000, false, 1, new Date(), "jesper"));
      entries1.add(new SVNDirEntry(null, null, "file2.html", SVNNodeKind.FILE, 32000, false, 2, new Date(), "jesper"));
      entries1.add(new SVNDirEntry(null, null, "dir1", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"));
      entries1.add(new SVNDirEntry(null, null, "File3.java", SVNNodeKind.FILE, 16000, false, 3, new Date(), "jesper"));
      final List<SVNDirEntry> entries2 = new ArrayList<SVNDirEntry>();
      entries2.add(new SVNDirEntry(null, null, "dir2", SVNNodeKind.DIR, 0, false, 1, new Date(), "jesper"));
      entries2.add(new SVNDirEntry(null, null, "file1.java", SVNNodeKind.FILE, 6400, false, 1, new Date(), "jesper"));
      entries2.add(new SVNDirEntry(null, null, "DirFile2.html", SVNNodeKind.FILE, 3200, false, 2, new Date(), "jesper"));
      entries2.add(new SVNDirEntry(null, null, "DirFile3.java", SVNNodeKind.FILE, 1600, false, 3, new Date(), "jesper"));
      repositoryEntries.put("/", entries1);
      repositoryEntries.put("/dir1/", entries2);
      repositoryEntries.put("/dir1/dir2/", new ArrayList());

      final Map<String, SVNLogEntryPath> changedPathsPopulation = new HashMap<String, SVNLogEntryPath>();
      changedPathsPopulation.put("/file1.java", new SVNLogEntryPath("/file1.java", 'M', null, 1));
      changedPathsPopulation.put("/file2.html", new SVNLogEntryPath("/file2.html", 'D', null, 1));
      changedPathsPopulation.put("/file3.abc", new SVNLogEntryPath("/file3.abc", 'A', null, 1));
      changedPathsPopulation.put("/file4.def", new SVNLogEntryPath("/file4.def", 'R', null, 1));
      logEntriesForPopulation.add(new SVNLogEntry(changedPathsPopulation, 123, "jesper", new Date(), "Log message for revision 123."));

      final Map<String, SVNLogEntryPath> changedPathsUpdate = new HashMap<String, SVNLogEntryPath>();
      changedPathsUpdate.put("/file1.java", new SVNLogEntryPath("/file1.java", 'M', null, 1));
      logEntriesForUpdate.add(new SVNLogEntry(changedPathsUpdate, 124, "jesper", new Date(), "Log message for revision 124."));

      infoEntry = new SVNDirEntry(null, null, "file999.java", SVNNodeKind.FILE, 12345, false, 1, new Date(), "jesper");
    }

    public long getLatestRevision() {
      return headRevision;
    }

    public void setLatestRevision(final long headRevision) {
      this.headRevision = headRevision;
    }

    public Collection getDir(String path, long revision, Map properties, Collection dirEntries) throws SVNException {
      return repositoryEntries.get(path);
    }

    public Collection log(String[] targetPaths, Collection entries, long startRevision, long endRevision, boolean changedPath, boolean strictNode) throws SVNException {
      return logEntriesForUpdate;
    }

    public long log(String[] targetPaths, long startRevision, long endRevision, boolean changedPath, boolean strictNode, ISVNLogEntryHandler handler) throws SVNException {
      for (final SVNLogEntry svnLogEntry : logEntriesForPopulation) {
        handler.handleLogEntry(svnLogEntry);
      }
      return logEntriesForPopulation.size();
    }

    public SVNDirEntry info(String path, long revision) throws SVNException {
      return infoEntry;
    }

  }

}
