package org.sventon.cache.logmessagecache;

import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;
import org.sventon.model.LogMessageSearchItem;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogMessageCacheImplTest extends TestCase {

  private LogMessageCacheImpl cache = null;

  protected void setUp() throws Exception {
    cache = new LogMessageCacheImpl(new File("test"));
    cache.init();
  }

  protected void tearDown() throws Exception {
    cache.shutdown();
  }

  private SVNLogEntry create(final long revision, final String message) {
    return new SVNLogEntry(null, revision, "theauthor", new Date(), message);
  }

  private SVNLogEntry create(final long revision, final String message, final Map<String, SVNLogEntryPath> paths) {
    return new SVNLogEntry(paths, revision, "theauthor", new Date(), message);
  }

  private Map<String, SVNLogEntryPath> createAndAddToMap(final String path, SVNLogEntryPath logEntryPath) {
    final Map<String, SVNLogEntryPath> changedPaths = new HashMap<String, SVNLogEntryPath>();
    changedPaths.put(path, logEntryPath);
    return changedPaths;
  }

  public void testAddAndFindWithStartDir() throws Exception {
    List<LogMessageSearchItem> logEntries;
    cache.add(new LogMessageSearchItem(create(123, "First message XYZ-123.",
        createAndAddToMap("/file1.java", new SVNLogEntryPath("/file1.java", 'M', null, 1)))));

    logEntries = cache.find("XYZ-123", "/");
    assertEquals(1, logEntries.size());
    assertEquals("First message <span class=\"searchhit\">XYZ-123</span>.", logEntries.get(0).getMessage());

    cache.add(new LogMessageSearchItem(create(456, "First message XYZ-456.",
        createAndAddToMap("/test/file1.java", new SVNLogEntryPath("/test/file1.java", 'M', null, 1)))));

    logEntries = cache.find("XYZ-456", "/");
    assertEquals(1, logEntries.size());
    assertEquals("First message <span class=\"searchhit\">XYZ-456</span>.", logEntries.get(0).getMessage());

    logEntries = cache.find("XYZ*", "/");
    assertEquals(2, logEntries.size());

    logEntries = cache.find("XYZ*", "/test/");
    assertEquals(1, logEntries.size());

    logEntries = cache.find("shouldnotfound", "/");
    assertEquals(0, logEntries.size());
  }

  public void testAddAndFind() throws Exception {
    cache.add(new LogMessageSearchItem(create(123, "First message XYZ-123.")));

    List<LogMessageSearchItem> logEntries = cache.find("XYZ-123");
    assertEquals(1, logEntries.size());
    assertEquals("First message <span class=\"searchhit\">XYZ-123</span>.", logEntries.get(0).getMessage());

    logEntries = cache.find("XYZ*");
    assertEquals("First message <span class=\"searchhit\">XYZ-123</span>.", logEntries.get(0).getMessage());

    logEntries = cache.find("XYZ-*");
    assertEquals("First message <span class=\"searchhit\">XYZ-123</span>.", logEntries.get(0).getMessage());

    logEntries = cache.find("XYZ-???");
    assertEquals("First message <span class=\"searchhit\">XYZ-123</span>.", logEntries.get(0).getMessage());

    cache.add(new LogMessageSearchItem(create(124, "This is a log message for revision 124.")));
    assertEquals(2, cache.getSize());

    logEntries = cache.find("message");
    assertEquals(2, logEntries.size());
    cache.add(new LogMessageSearchItem(create(125, "This is a log message for revision 125.")));
    assertEquals(3, cache.getSize());

    cache.add(new LogMessageSearchItem(create(126, "This is a log message for revision 126......."))); // punctuation will be trimmed
    assertEquals(4, cache.getSize());
    logEntries = cache.find("126");
    assertEquals("This is a log message for revision <span class=\"searchhit\">126</span>.......", logEntries.get(0).getMessage());

    cache.add(new LogMessageSearchItem(create(127, "Testing brackets (must work)")));
    assertEquals(5, cache.getSize());
    logEntries = cache.find("brackets");
    assertEquals(1, logEntries.size());
    assertEquals("Testing <span class=\"searchhit\">brackets</span> (must work)", logEntries.get(0).getMessage());

    cache.add(new LogMessageSearchItem(create(128, "Testing brackets (must work)")));
    assertEquals(6, cache.getSize());
    logEntries = cache.find("work");
    assertEquals(2, logEntries.size());
    assertEquals("Testing brackets (must <span class=\"searchhit\">work</span>)", logEntries.get(1).getMessage());

    cache.add(new LogMessageSearchItem(create(129, "Lite svenska tecken, \u00E5 \u00E4 \u00F6!")));
    assertEquals(7, cache.getSize());
    logEntries = cache.find("svenska");
    assertEquals(1, logEntries.size());
    assertEquals("Lite <span class=\"searchhit\">svenska</span> tecken, &#229; &#228; &#246;!", logEntries.get(0).getMessage());

    logEntries = cache.find("message");
    assertEquals(4, logEntries.size());

    cache.add(new LogMessageSearchItem(create(130, "<This is a <code>log</code> message for &amp; revision 130.......")));
    assertEquals(8, cache.getSize());

    logEntries = cache.find("*autho*");
    assertEquals(8, logEntries.size());
    assertTrue(StringUtils.isNotBlank(logEntries.get(0).getMessage()));

    logEntries = cache.find("theauthor");
    assertEquals(8, logEntries.size());

    logEntries = cache.find("log");
    assertEquals(4, logEntries.size());
    assertEquals("&lt;This is a &lt;code&gt;<span class=\"searchhit\">log</span>&lt;/code&gt; message for &amp;amp; revision 130.......", logEntries.get(3).getMessage());

    cache.add(new LogMessageSearchItem(create(131, "Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
        "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took " +
        "a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, " +
        "but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the " +
        "1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop " +
        "publishing software like Aldus PageMaker including versions of Lorem Ipsum.")));
    assertEquals(9, cache.getSize());
    logEntries = cache.find("dummy");

    assertEquals("Lorem Ipsum is simply <span class=\"searchhit\">dummy</span> text of the printing and typesetting industry. " +
        "Lorem Ipsum has been the industry's standard <span class=\"searchhit\">dummy</span> text ever since the 1500s, when an unknown printer took " +
        "a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, " +
        "but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the " +
        "1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop " +
        "publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
        logEntries.get(0).getMessage());
  }

  public void testAddEmptyAndNull() throws Exception {
    cache.add(new LogMessageSearchItem(create(123, "")));
    assertEquals(1, cache.getSize());

    cache.add(new LogMessageSearchItem(create(124, null)));
    assertEquals(2, cache.getSize());

    cache.add(new LogMessageSearchItem(new SVNLogEntry(null, 125, null, new Date(), "abc")));
    assertEquals(3, cache.getSize());
    final LogMessageSearchItem logEntry = cache.find("abc").get(0);
    assertEquals("<span class=\"searchhit\">abc</span>", logEntry.getMessage());
    assertEquals(null, logEntry.getAuthor());
  }

  public void testUsingAndLogic() throws Exception {
    cache.add(new LogMessageSearchItem(create(234, "one two three")));
    assertEquals(1, cache.getSize());
    final LogMessageSearchItem logEntry = cache.find("one AND two").get(0);
    assertEquals("<span class=\"searchhit\">one</span> <span class=\"searchhit\">two</span> three", logEntry.getMessage());
  }

}