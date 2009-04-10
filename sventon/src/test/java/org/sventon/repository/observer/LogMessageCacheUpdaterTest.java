package org.sventon.repository.observer;

import junit.framework.TestCase;
import org.sventon.TestUtils;
import org.sventon.cache.logmessagecache.LogMessageCache;
import org.sventon.cache.logmessagecache.LogMessageCacheImpl;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class LogMessageCacheUpdaterTest extends TestCase {

  public void testUpdate() throws Exception {
    final LogMessageCache cache = new LogMessageCacheImpl(new File("test"), false);
    cache.init();

    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    logEntries.add(TestUtils.getLogEntryStub(123, "Log message for revision 123."));
    logEntries.add(TestUtils.getLogEntryStub(124, "Log message for revision 124."));

    assertEquals(0, cache.getSize());
    final LogMessageCacheUpdater logMessageCacheUpdater = new LogMessageCacheUpdater(null);
    logMessageCacheUpdater.updateInternal(cache, logEntries);
    assertEquals(2, cache.find("revision").size());
  }
}