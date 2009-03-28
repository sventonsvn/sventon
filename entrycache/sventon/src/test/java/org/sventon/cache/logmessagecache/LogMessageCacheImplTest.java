package org.sventon.cache.logmessagecache;

import junit.framework.TestCase;
import org.sventon.model.LogMessage;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.io.File;
import java.util.Date;
import java.util.List;

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

  public void testAdd() throws Exception {
    cache.add(new LogMessage(create(123, "First message XYZ-123.")));

    List<LogMessage> logMessages = cache.find("XYZ-123");
    assertEquals(1, logMessages.size());
    assertEquals("First message <span class=\"searchhit\">XYZ-123</span>.", logMessages.get(0).getMessage());

    logMessages = cache.find("XYZ*");
    assertEquals("First message <span class=\"searchhit\">XYZ-123</span>.", logMessages.get(0).getMessage());

    logMessages = cache.find("XYZ-*");
    assertEquals("First message <span class=\"searchhit\">XYZ-123</span>.", logMessages.get(0).getMessage());

    logMessages = cache.find("XYZ-???");
    assertEquals("First message <span class=\"searchhit\">XYZ-123</span>.", logMessages.get(0).getMessage());

    cache.add(new LogMessage(create(124, "This is a log message for revision 124.")));
    assertEquals(2, cache.getSize());

    logMessages = cache.find("message");
    assertEquals(2, logMessages.size());
    cache.add(new LogMessage(create(125, "This is a log message for revision 125.")));
    assertEquals(3, cache.getSize());

    cache.add(new LogMessage(create(126, "This is a log message for revision 126......."))); // punctuation will be trimmed
    assertEquals(4, cache.getSize());
    logMessages = cache.find("126");
    assertEquals("This is a log message for revision <span class=\"searchhit\">126</span>.......", logMessages.get(0).getMessage());

    cache.add(new LogMessage(create(127, "Testing brackets (must work)")));
    assertEquals(5, cache.getSize());
    logMessages = cache.find("brackets");
    assertEquals(1, logMessages.size());
    assertEquals("Testing <span class=\"searchhit\">brackets</span> (must work)", logMessages.get(0).getMessage());

    cache.add(new LogMessage(create(128, "Testing brackets (must work)")));
    assertEquals(6, cache.getSize());
    logMessages = cache.find("work");
    assertEquals(2, logMessages.size());
    assertEquals("Testing brackets (must <span class=\"searchhit\">work</span>)", logMessages.get(1).getMessage());

    cache.add(new LogMessage(create(129, "Lite svenska tecken, \u00E5 \u00E4 \u00F6!")));
    assertEquals(7, cache.getSize());
    logMessages = cache.find("svenska");
    assertEquals(1, logMessages.size());
    assertEquals("Lite <span class=\"searchhit\">svenska</span> tecken, &#229; &#228; &#246;!", logMessages.get(0).getMessage());

    logMessages = cache.find("message");
    assertEquals(4, logMessages.size());

    cache.add(new LogMessage(create(130, "<This is a <code>log</code> message for &amp; revision 130.......")));
    assertEquals(8, cache.getSize());

    logMessages = cache.find("log");
    assertEquals(4, logMessages.size());
    assertEquals("&lt;This is a &lt;code&gt;<span class=\"searchhit\">log</span>&lt;/code&gt; message for &amp;amp; revision 130.......", logMessages.get(3).getMessage());

    cache.add(new LogMessage(create(131, "Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
        "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took " +
        "a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, " +
        "but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the " +
        "1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop " +
        "publishing software like Aldus PageMaker including versions of Lorem Ipsum.")));
    assertEquals(9, cache.getSize());
    logMessages = cache.find("dummy");

    assertEquals("Lorem Ipsum is simply <span class=\"searchhit\">dummy</span> text of the printing and typesetting industry. " +
        "Lorem Ipsum has been the industry's standard <span class=\"searchhit\">dummy</span> text ever since the 1500s, when an unknown printer took " +
        "a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, " +
        "but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the " +
        "1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop " +
        "publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
        logMessages.get(0).getMessage());

  }

  public void testAddEmptyAndNull() throws Exception {
    cache.add(new LogMessage(create(123, "")));
    assertEquals(1, cache.getSize());

    cache.add(new LogMessage(create(124, null)));
    assertEquals(2, cache.getSize());
  }

}