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
package org.sventon.rss;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.sventon.TestUtils;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.model.ChangeType;
import org.sventon.model.ChangedPath;
import org.sventon.model.LogEntry;

import java.io.File;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RssFeedGeneratorTest {

  @Test
  public void testGenerateFeedRSS20() throws Exception {
    final DefaultRssFeedGenerator generator = new DefaultRssFeedGenerator();
    generator.setFeedType("rss_2.0");
    generator.setLogMessageLength(20);
    generator.setDateFormat("yyyyMMdd HH:mm:ss");

    List<LogEntry> logEntries = new ArrayList<LogEntry>();
    SortedSet<ChangedPath> changedPaths;

    final String logMessage = "&lt; &gt; / &amp; ' ; \\";

    changedPaths = new TreeSet<ChangedPath>();
    changedPaths.add(new ChangedPath("/file1.java", null, 1, ChangeType.MODIFIED));
    changedPaths.add(new ChangedPath("/file2.html", null, 1, ChangeType.DELETED));
    changedPaths.add(new ChangedPath("/file3.abc", null, 1, ChangeType.ADDED));
    changedPaths.add(new ChangedPath("/file4.def", null, 1, ChangeType.REPLACED));
    logEntries.add(TestUtils.createLogEntry(1, "jesper", new Date(), logMessage, changedPaths));

    changedPaths = new TreeSet<ChangedPath>();
    changedPaths.add(new ChangedPath("/file1.java", null, 2, ChangeType.MODIFIED));
    changedPaths.add(new ChangedPath("/file2.html", null, 2, ChangeType.DELETED));
    changedPaths.add(new ChangedPath("/file3.abc", null, 2, ChangeType.ADDED));
    changedPaths.add(new ChangedPath("/file4.def", "/file44.def", 1, ChangeType.REPLACED));
    logEntries.add(TestUtils.createLogEntry(2, "jesper", new Date(), "Another\nlog message.", changedPaths));

    final File tempFile = File.createTempFile("sventon-rss-test", null);
    final PrintWriter pw = new PrintWriter(tempFile);

    final MockHttpServletRequest req = new MockHttpServletRequest("get", "http://localhost:8888/svn/");
    final MockHttpServletResponse res = new MockHttpServletResponse() {
      public PrintWriter getWriter() throws UnsupportedEncodingException {
        return pw;
      }
    };

    generator.outputFeed(new RepositoryConfiguration("defaultsvn"), logEntries, req, res);
    pw.flush();
    pw.close();

    if (tempFile.exists()) {
      tempFile.delete();
    } else {
      fail("No rss feed file was created in " + TestUtils.TEMP_DIR);
    }
  }

  @Test
  public void testGetAbbreviatedLogMessage() throws Exception {
    final DefaultRssFeedGenerator generator = new DefaultRssFeedGenerator();
    assertEquals("this is...", generator.getAbbreviatedLogMessage("this is a message", 10));
    assertEquals("this is a mes...", generator.getAbbreviatedLogMessage("this is a message", 16));
    assertEquals("this is a message", generator.getAbbreviatedLogMessage("this is a message", 17));
    assertEquals(null, generator.getAbbreviatedLogMessage(null, 10));
    assertEquals("", generator.getAbbreviatedLogMessage("", 10));
  }
}
