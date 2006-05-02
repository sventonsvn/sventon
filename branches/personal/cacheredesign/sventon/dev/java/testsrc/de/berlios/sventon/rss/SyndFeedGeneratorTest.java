package de.berlios.sventon.rss;

import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class SyndFeedGeneratorTest extends TestCase {

  public void testGenerateFeedRSS20() throws Exception {
    SyndFeedGenerator generator = new SyndFeedGenerator();

    List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    Map<String, SVNLogEntryPath> changedPaths;

    changedPaths = new HashMap<String, SVNLogEntryPath>();
    changedPaths.put("/file1.java", new SVNLogEntryPath("/file1.java", 'M', null, 1));
    changedPaths.put("/file2.html", new SVNLogEntryPath("/file2.html", 'D', null, 1));
    changedPaths.put("/file3.abc", new SVNLogEntryPath("/file3.abc", 'A', null, 1));
    changedPaths.put("/file4.def", new SVNLogEntryPath("/file4.def", 'R', null, 1));
    logEntries.add(new SVNLogEntry(changedPaths, 1, "jesper", new Date(), "Commit message."));

    changedPaths = new HashMap<String, SVNLogEntryPath>();
    changedPaths.put("/anotherfile1.java", new SVNLogEntryPath("/file1.java", 'M', null, 2));
    changedPaths.put("/anotherfile2.html", new SVNLogEntryPath("/file2.html", 'D', null, 2));
    changedPaths.put("/anotherfile3.abc", new SVNLogEntryPath("/file3.abc", 'A', null, 2));
    changedPaths.put("/anotherfile4.def", new SVNLogEntryPath("/file4.def", 'R', "/file44.def", 1));
    logEntries.add(new SVNLogEntry(changedPaths, 2, "jesper", new Date(), "Another commit message."));

    generator.generateFeed(logEntries, "http://localhost:8888/svn/");

    File tempFile = File.createTempFile("sventon-rss-test", null);
    PrintWriter pw = new PrintWriter(tempFile);
    generator.outputFeed(pw);
    pw.flush();
    pw.close();

    if (tempFile.exists()) {
      tempFile.delete();
    } else {
      throw new Exception("No rss feed file was created in " + System.getProperty("java.io.tmpdir"));
    }

  }

  public void testGetAbbreviatedCommitMessage() throws Exception {
    SyndFeedGenerator f = new SyndFeedGenerator();
    assertEquals("this is...", f.getAbbreviatedCommitMessage("this is a message", 10));
    assertEquals("this is a mes...", f.getAbbreviatedCommitMessage("this is a message", 16));
    assertEquals("this is a message", f.getAbbreviatedCommitMessage("this is a message", 17));
    assertEquals(null, f.getAbbreviatedCommitMessage(null, 10));
    assertEquals("", f.getAbbreviatedCommitMessage("", 10));
  }
}
