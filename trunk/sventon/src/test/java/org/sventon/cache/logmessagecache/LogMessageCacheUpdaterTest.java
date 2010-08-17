package org.sventon.cache.logmessagecache;

import junit.framework.TestCase;
import org.sventon.TestUtils;
import org.sventon.model.LogEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LogMessageCacheUpdaterTest extends TestCase {

  public void testUpdate() throws Exception {
    final LogMessageCache cache = new LogMessageCacheImpl(new File("test"), false);
    cache.init();

    final List<LogEntry> logEntries = new ArrayList<LogEntry>();
    logEntries.add(TestUtils.createLogEntry(123, null, null, "Log message for revision 123."));
    logEntries.add(TestUtils.createLogEntry(124, null, null, "Log message for revision 124."));

    assertEquals(0, cache.getSize());
    final LogMessageCacheUpdater logMessageCacheUpdater = new LogMessageCacheUpdater(null);
    logMessageCacheUpdater.updateInternal(cache, logEntries);
    assertEquals(2, cache.find("revision").size());
  }
}