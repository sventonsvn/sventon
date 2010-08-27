/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.service.javahl;

import org.apache.commons.collections.MapUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sventon.SVNConnection;
import org.sventon.model.ChangeType;
import org.sventon.model.ChangedPath;
import org.sventon.model.LogEntry;
import org.sventon.model.SVNURL;
import org.sventon.util.DateUtil;
import org.tigris.subversion.javahl.*;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class JavaHLRepositoryServiceTest {
  private JavaHLRepositoryService service;

  @Mock
  private SVNConnection connection;
  @Mock
  private SVNClientInterface client;

  @Test
  public void testGetLogEntries() throws Exception {
    final Map<String, String> propMap = new HashMap<String, String>();
    final ChangePath cp1 = mock(ChangePath.class);
    final ChangePath cp2 = mock(ChangePath.class);
    final ChangePath[] changePaths = {cp1, cp2};
    final int rev = 4711;
    final Date date = new Date();
    final String dateString = DateUtil.formatISO8601(date);

    MapUtils.putAll(propMap, new String[][]{
        {"svn:author", "daAuthor"},
        {"svn:date", dateString},
        {"svn:log", "Added new text in my finest file"}
    });

    when(cp1.getPath()).thenReturn("/trunk/src/main/da/path/myfile.txt");
    when(cp1.getAction()).thenReturn('M');
    when(cp1.getCopySrcPath()).thenReturn(null);
    when(cp1.getCopySrcRevision()).thenReturn(-1L);

    when(cp2.getPath()).thenReturn("/branches/lemontree/src/main/da/path/myfile.txt");
    when(cp2.getAction()).thenReturn('A');
    when(cp2.getCopySrcPath()).thenReturn(null);
    when(cp2.getCopySrcRevision()).thenReturn(-1L);

    when(connection.getRepositoryRootUrl()).thenReturn(new SVNURL("svn://myhost/repro"));

    // Yiks! We probably need to refactor this later...
    // Matching for SVNClient.logMessages() is also a little bit too loose.
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        final LogMessageCallback cb = (LogMessageCallback) args[8];
        cb.singleMessage(changePaths, rev, propMap, false);

        return null;
      }
    }).when(client).logMessages(eq("svn://myhost/repro/da/path"), (Revision) any(), (RevisionRange[]) any(),
        eq(false), eq(false), eq(false), (String[]) any(), anyInt(), (LogMessageCallback) any());

    final List<LogEntry> logEntries = service.getLogEntries(null, connection, 1, 100, "da/path", 100, false, false);

    // Verify number of LogEntries
    assertEquals(1, logEntries.size());

    // Verify ChangePath
    final LogEntry logEntry = logEntries.get(0);
    final Set<ChangedPath> changedPaths = logEntry.getChangedPaths();
    assertEquals(2, changedPaths.size());

    ChangedPath[] paths = new ChangedPath[2];
    changedPaths.toArray(paths);
    Arrays.sort(paths, new Comparator<ChangedPath>() {
      @Override
      public int compare(ChangedPath o1, ChangedPath o2) {
        return o1.getPath().compareTo(o2.getPath());
      }
    });
    assertEquals("/branches/lemontree/src/main/da/path/myfile.txt", paths[0].getPath());
    assertEquals(ChangeType.ADDED, paths[0].getType());
    assertEquals("/trunk/src/main/da/path/myfile.txt", paths[1].getPath());
    assertEquals(ChangeType.MODIFIED, paths[1].getType());

    //Verify Properties
    assertEquals("daAuthor", logEntry.getAuthor());
    // TODO: check this! We fail because we're GMT+1&DLS and date is in UTC...
    //assertEquals(date, logEntry.getDate());
    assertEquals("Added new text in my finest file", logEntry.getMessage());
    assertEquals(4711, logEntry.getRevision());
  }

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    service = new JavaHLRepositoryService();

    when(connection.getDelegate()).thenReturn(client);
  }


}
