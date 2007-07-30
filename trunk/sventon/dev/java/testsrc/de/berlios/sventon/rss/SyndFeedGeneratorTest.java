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
    generator.setFeedType("rss_2.0");
    generator.setLogMessageLength(20);

    List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    Map<String, SVNLogEntryPath> changedPaths;

    final String logMessage = "&lt; &gt; / &amp; ' ; \\";

    changedPaths = new HashMap<String, SVNLogEntryPath>();
    changedPaths.put("/file1.java", new SVNLogEntryPath("/file1.java", 'M', null, 1));
    changedPaths.put("/file2.html", new SVNLogEntryPath("/file2.html", 'D', null, 1));
    changedPaths.put("/file3.abc", new SVNLogEntryPath("/file3.abc", 'A', null, 1));
    changedPaths.put("/file4.def", new SVNLogEntryPath("/file4.def", 'R', null, 1));
    logEntries.add(new SVNLogEntry(changedPaths, 1, "jesper", new Date(), logMessage));

    changedPaths = new HashMap<String, SVNLogEntryPath>();
    changedPaths.put("/anotherfile1.java", new SVNLogEntryPath("/file1.java", 'M', null, 2));
    changedPaths.put("/anotherfile2.html", new SVNLogEntryPath("/file2.html", 'D', null, 2));
    changedPaths.put("/anotherfile3.abc", new SVNLogEntryPath("/file3.abc", 'A', null, 2));
    changedPaths.put("/anotherfile4.def", new SVNLogEntryPath("/file4.def", 'R', "/file44.def", 1));
    logEntries.add(new SVNLogEntry(changedPaths, 2, "jesper", new Date(), "Another log message."));

    final File tempFile = File.createTempFile("sventon-rss-test", null);
    final PrintWriter pw = new PrintWriter(tempFile);
    generator.outputFeed("defaultsvn", logEntries, "http://localhost:8888/svn/", pw);
    pw.flush();
    pw.close();

    if (tempFile.exists()) {
      tempFile.delete();
    } else {
      throw new Exception("No rss feed file was created in " + System.getProperty("java.io.tmpdir"));
    }

  }

  public void testGetAbbreviatedLogMessage() throws Exception {
    final SyndFeedGenerator generator = new SyndFeedGenerator();
    assertEquals("this is...", generator.getAbbreviatedLogMessage("this is a message", 10));
    assertEquals("this is a mes...", generator.getAbbreviatedLogMessage("this is a message", 16));
    assertEquals("this is a message", generator.getAbbreviatedLogMessage("this is a message", 17));
    assertEquals(null, generator.getAbbreviatedLogMessage(null, 10));
    assertEquals("", generator.getAbbreviatedLogMessage("", 10));
  }

  public void testCreateChangedPathsTable() throws Exception {
    final String result = "<table border=\"0\">\n" +
        "  <tr>\n" +
        "    <th>Action</th>\n" +
        "    <th>Path</th>\n" +
        "  </tr>\n" +
        "  <tr>\n" +
        "    <td valign=\"top\"><i>Modified</i></td>\n" +
        "    <td>/file1.java</td>\n" +
        "  </tr>\n" +
        "  <tr>\n" +
        "    <td valign=\"top\"><i>Deleted</i></td>\n" +
        "    <td>/file2.html</td>\n" +
        "  </tr>\n" +
        "  <tr>\n" +
        "    <td valign=\"top\"><i>Added</i></td>\n" +
        "    <td>/file3.abc</td>\n" +
        "  </tr>\n" +
        "  <tr>\n" +
        "    <td valign=\"top\"><i>Replaced</i></td>\n" +
        "    <td>/file4.def</td>\n" +
        "  </tr>\n" +
        "</table>";

    final SyndFeedGenerator generator = new SyndFeedGenerator();
    final Map<String, SVNLogEntryPath> changedPaths = new HashMap<String, SVNLogEntryPath>();
    changedPaths.put("/file1.java", new SVNLogEntryPath("/file1.java", 'M', null, 1));
    changedPaths.put("/file2.html", new SVNLogEntryPath("/file2.html", 'D', null, 1));
    changedPaths.put("/file3.abc", new SVNLogEntryPath("/file3.abc", 'A', null, 1));
    changedPaths.put("/file4.def", new SVNLogEntryPath("/file4.def", 'R', null, 1));
    final SVNLogEntry logEntry = new SVNLogEntry(changedPaths, 1, "jesper", new Date(), "Testing");

    assertEquals(result, generator.createChangedPathsTable(logEntry));
  }
}
