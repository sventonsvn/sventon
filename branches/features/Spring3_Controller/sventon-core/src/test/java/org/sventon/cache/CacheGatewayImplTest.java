package org.sventon.cache;

import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.sventon.TestUtils;
import org.sventon.appl.ConfigDirectory;
import org.sventon.cache.direntrycache.CompassDirEntryCache;
import org.sventon.cache.direntrycache.DirEntryCache;
import org.sventon.model.DirEntry;
import org.sventon.model.RepositoryName;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class CacheGatewayImplTest {

  private final RepositoryName repositoryName = new RepositoryName("testRepos");

  private CacheGateway createCache() throws CacheException {
    final ConfigDirectory configDirectory = TestUtils.getTestConfigDirectory();
    configDirectory.setCreateDirectories(false);
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);

    final DirEntryCacheManager cacheManager = new DirEntryCacheManager(configDirectory);
    final DirEntryCache entryCache = new CompassDirEntryCache(new File("test"));
    entryCache.init();
    cacheManager.addCache(repositoryName, entryCache);

    for (DirEntry dirEntry : TestUtils.getDirectoryList()) {
      entryCache.add(dirEntry);
    }
    final CacheGatewayImpl cache = new CacheGatewayImpl();
    cache.setEntryCacheManager(cacheManager);
    return cache;
  }

  @Test
  public void testFindEntryInPath() throws Exception {
    final CacheGateway cache = createCache();
    assertEquals(1, cache.findEntries(repositoryName, "html", "/trunk/src/").size());
    assertEquals(5, cache.findEntries(repositoryName, "java", "/").size());
    assertEquals(1, cache.findEntries(repositoryName, "code", "/").size());
  }

  @Test
  public void testFindDirectories() throws Exception {
    final CacheGateway cache = createCache();
    assertEquals(4, cache.findDirectories(repositoryName, "/").size());
    assertEquals(2, cache.findDirectories(repositoryName, "/trunk/").size());
  }

}
