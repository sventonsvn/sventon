package de.berlios.sventon.repository.cache.logmessagecache;

import junit.framework.TestCase;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import java.util.*;

public class LogMessageCacheUpdaterTest extends TestCase {

  public void testUpdate() throws Exception {
    final Directory directory = new RAMDirectory();
    final LogMessageCache cache = new LogMessageCacheImpl(directory);
    final LogMessageCacheUpdater cacheUpdater = new LogMessageCacheUpdater(cache);

    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    final Map<String, SVNLogEntryPath> changedPaths1 = new HashMap<String, SVNLogEntryPath>();
    changedPaths1.put("/file1.java", new SVNLogEntryPath("/file1.java", 'M', null, 1));
    changedPaths1.put("/file2.html", new SVNLogEntryPath("/file2.html", 'D', null, 1));
    changedPaths1.put("/file3.abc", new SVNLogEntryPath("/file3.abc", 'A', null, 1));
    changedPaths1.put("/file4.def", new SVNLogEntryPath("/file4.def", 'R', null, 1));
    logEntries.add(new SVNLogEntry(changedPaths1, 123, "jesper", new Date(), "Log message for revision 123."));

    final Map<String, SVNLogEntryPath> changedPaths2 = new HashMap<String, SVNLogEntryPath>();
    changedPaths2.put("/file1.java", new SVNLogEntryPath("/file1.java", 'M', null, 1));
    logEntries.add(new SVNLogEntry(changedPaths2, 124, "jesper", new Date(), "Log message for revision 124."));

    assertEquals(0, cache.getSize());
    cacheUpdater.update("defaultsvn", logEntries);
    assertEquals(2, cache.find("revision").size());
  }
}