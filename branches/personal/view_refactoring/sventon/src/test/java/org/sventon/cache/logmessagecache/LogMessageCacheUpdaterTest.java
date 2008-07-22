package org.sventon.cache.logmessagecache;

import org.sventon.TestUtils;
import junit.framework.TestCase;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.ArrayList;
import java.util.List;

public class LogMessageCacheUpdaterTest extends TestCase {

  public void testUpdate() throws Exception {
    final Directory directory = new RAMDirectory();
    final LogMessageCache cache = new LogMessageCacheImpl(directory, StandardAnalyzer.class);

    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    logEntries.add(TestUtils.getLogEntryStub(123, "Log message for revision 123."));
    logEntries.add(TestUtils.getLogEntryStub(124, "Log message for revision 124."));

    assertEquals(0, cache.getSize());
    final LogMessageCacheUpdater logMessageCacheUpdater = new LogMessageCacheUpdater(null);
    logMessageCacheUpdater.updateInternal(cache, logEntries);
    assertEquals(2, cache.find("revision").size());
  }
}