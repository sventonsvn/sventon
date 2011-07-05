/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.cache.direntrycache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.sventon.TestUtils;
import org.sventon.appl.Application;
import org.sventon.appl.ConfigDirectory;
import org.sventon.model.*;
import org.sventon.model.Properties;
import org.sventon.repository.RevisionUpdate;
import org.sventon.service.RepositoryService;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.sventon.TestUtils.createLogEntry;

public class DirEntryCacheUpdaterTest {

  private DirEntryCache entryCache;

  @Before
  public void setUp() throws Exception {
    entryCache = new CompassDirEntryCache(new File("test"));
    entryCache.init();
  }

  @After
  public void tearDown() throws Exception {
    entryCache.shutdown();
  }

  @Test
  public void testUpdate() throws Exception {
    final RepositoryService serviceMock = mock(RepositoryService.class);
    assertEquals(0, entryCache.getSize());

    final List<LogEntry> logEntries = new ArrayList<LogEntry>();
    final SortedSet<ChangedPath> changedPaths1 = new TreeSet<ChangedPath>();
    changedPaths1.add(new ChangedPath("/file1.java", null, -1, ChangeType.MODIFIED));
    changedPaths1.add(new ChangedPath("/file2.abc", null, -1, ChangeType.ADDED));
    changedPaths1.add(new ChangedPath("/trunk/file3.def", null, -1, ChangeType.REPLACED));
    logEntries.add(createLogEntry(123, "author", new Date(), "Log message for revision 123.", changedPaths1));

    final SortedSet<ChangedPath> changedPaths2 = new TreeSet<ChangedPath>();
    changedPaths2.add(new ChangedPath("/branch", "/trunk", 123, ChangeType.ADDED));
    changedPaths2.add(new ChangedPath("/trunk/file3.def", null, -1, ChangeType.DELETED));
    logEntries.add(createLogEntry(124, "author", new Date(), "Log message for revision 124.", changedPaths2));

    final ConfigDirectory configDirectory = TestUtils.getTestConfigDirectory();
    configDirectory.setCreateDirectories(false);
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);
    final Application application = new Application(configDirectory);

    when(serviceMock.getLatestRevision(null)).thenReturn(124L);

    when(serviceMock.getEntryInfo(null, "/file1.java", 123)).thenReturn(new DirEntry(
        "/", "file1.java", "author", new Date(), DirEntry.Kind.FILE, 123, 12345));

    when(serviceMock.getEntryInfo(null, "/file2.abc", 123)).thenReturn(new DirEntry(
        "/", "file2.abc", "author", new Date(), DirEntry.Kind.FILE, 123, 12345));

    when(serviceMock.getEntryInfo(null, "/trunk/file3.def", 123)).thenReturn(new DirEntry(
        "/trunk", "file3.def", "author", new Date(), DirEntry.Kind.FILE, 123, 12345));

    when(serviceMock.getEntryInfo(null, "/branch", 124)).thenReturn(new DirEntry(
        "/", "branch", "author", new Date(), DirEntry.Kind.DIR, 123, 12345));

    when(serviceMock.list(null, "/branch/", 124)).thenReturn(new DirList(Collections.<DirEntry>emptyList(), new Properties()));

    when(serviceMock.getEntryInfo(null, "/trunk/file3.def", 123)).thenReturn(new DirEntry(
        "/trunk", "file3.def", "author", new Date(), DirEntry.Kind.FILE, 123, 12345));

    final DirEntryCacheUpdater cacheUpdater = new DirEntryCacheUpdater(null, application);
    cacheUpdater.setRepositoryService(serviceMock);
    final RepositoryName repositoryName = new RepositoryName("defaultsvn");
    final RevisionUpdate revisionUpdate = new RevisionUpdate(repositoryName, logEntries, false);
    cacheUpdater.updateInternal(entryCache, null, revisionUpdate);

    Thread.sleep(500L); // TODO: Get rid of this!
    assertEquals(4, entryCache.getSize());
  }

}
