package org.sventon.cache.logentrycache;

import junit.framework.TestCase;
import org.sventon.TestUtils;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class LogEntryCacheUpdaterTest extends TestCase {

  public void testUpdate() throws Exception {
    final LogEntryCache cache = new LogEntryCacheImpl(new File("test"), false);
    cache.init();

    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    logEntries.add(TestUtils.getLogEntryStub(123, "Log message for revision 123."));
    logEntries.add(TestUtils.getLogEntryStub(124, "Log message for revision 124."));

    assertEquals(0, cache.getSize());
    final LogEntryCacheUpdater logEntryCacheUpdater = new LogEntryCacheUpdater(null);
    logEntryCacheUpdater.updateInternal(cache, logEntries);
    assertEquals(2, cache.find("revision").size());
  }
}