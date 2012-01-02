/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
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

public class DefaultCacheGatewayTest {

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
    final DefaultCacheGateway cache = new DefaultCacheGateway();
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
