package org.sventon.cache;

import junit.framework.TestCase;
import org.springframework.mock.web.MockServletContext;
import org.sventon.SVNRepositoryStub;
import org.sventon.TestUtils;
import org.sventon.appl.ConfigDirectory;
import org.sventon.appl.EntryCacheManager;
import org.sventon.cache.direntrycache.CompassDirEntryCache;
import org.sventon.cache.direntrycache.DirEntryCache;
import org.sventon.model.RepositoryEntry;
import org.sventon.model.RepositoryName;
import org.tmatesoft.svn.core.*;

import java.io.File;
import java.util.*;

public class CacheGatewayImplTest extends TestCase {

  private final RepositoryName repositoryName = new RepositoryName("testRepos");

  private CacheGateway createCache() throws CacheException {
    final ConfigDirectory configDirectory = TestUtils.getTestConfigDirectory();
    configDirectory.setCreateDirectories(false);
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);

    final EntryCacheManager cacheManager = new EntryCacheManager(configDirectory);
    final DirEntryCache entryCache = new CompassDirEntryCache(new File("test"));
    entryCache.init();
    cacheManager.addCache(repositoryName, entryCache);

    for (RepositoryEntry repositoryEntry : TestUtils.getDirectoryList()) {
      entryCache.add(repositoryEntry);
    }
    final CacheGatewayImpl cache = new CacheGatewayImpl();
    cache.setEntryCacheManager(cacheManager);
    return cache;
  }

  public void testFindEntryInPath() throws Exception {
    final CacheGateway cache = createCache();
    assertEquals(1, cache.findEntries(repositoryName, "html", "/trunk/src/").size());
    assertEquals(5, cache.findEntries(repositoryName, "java", "/").size());
    assertEquals(1, cache.findEntries(repositoryName, "code", "/").size());
  }

//  public void testFindEntryByCamelCase() throws Exception {
//    final CacheGateway cache = createCache();
//    final CamelCasePattern ccPattern = new CamelCasePattern("DF");
//    assertEquals(2, cache.findEntriesByCamelCase(repositoryName, ccPattern, "/trunk/").size());
//  }

  public void testFindDirectories() throws Exception {
    final CacheGateway cache = createCache();
    assertEquals(4, cache.findDirectories(repositoryName, "/").size());
    assertEquals(2, cache.findDirectories(repositoryName, "/trunk/").size());
  }

  static class TestRepository extends SVNRepositoryStub {
    private List<SVNLogEntry> logEntriesForPopulation = new ArrayList<SVNLogEntry>();
    private List<SVNLogEntry> logEntriesForUpdate = new ArrayList<SVNLogEntry>();
    private SVNDirEntry infoEntry;
    private long headRevision = 123;

    public TestRepository() {
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

    @Override
    public long getLatestRevision() {
      return headRevision;
    }

    @Override
    public Collection log(String[] targetPaths, Collection entries, long startRevision, long endRevision, boolean changedPath, boolean strictNode) throws SVNException {
      return logEntriesForUpdate;
    }

    @Override
    public long log(String[] targetPaths, long startRevision, long endRevision, boolean changedPath, boolean strictNode, ISVNLogEntryHandler handler) throws SVNException {
      for (final SVNLogEntry svnLogEntry : logEntriesForPopulation) {
        handler.handleLogEntry(svnLogEntry);
      }
      return logEntriesForPopulation.size();
    }

    @Override
    public SVNDirEntry info(String path, long revision) throws SVNException {
      return infoEntry;
    }

  }

}
