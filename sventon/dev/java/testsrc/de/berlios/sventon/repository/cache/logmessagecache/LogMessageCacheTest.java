package de.berlios.sventon.repository.cache.logmessagecache;

import de.berlios.sventon.repository.LogMessage;
import junit.framework.TestCase;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.util.List;

public class LogMessageCacheTest extends TestCase {

  public void testAdd() throws Exception {
    final Directory directory = new RAMDirectory();

    //final String tempDir = System.getProperty("java.io.tmpdir") + "sventonCacheTest";
    //final Directory directory = FSDirectory.getDirectory(tempDir, true);

    final LogMessageCacheImpl cache = new LogMessageCacheImpl(directory);

    cache.add(new LogMessage(123, "This is a log message for revision 123."));

    List<LogMessage> logMessages = cache.find("123");
    assertEquals(1, logMessages.size());
    assertEquals("This is a log message for revision <B>123</B>", logMessages.get(0).getMessage());

    cache.add(new LogMessage(124, "This is a log message for revision 124."));
    assertEquals(2, cache.getSize());

    logMessages = cache.find("message");
    assertEquals(2, logMessages.size());
    cache.add(new LogMessage(125, "This is a log message for revision 125."));
    assertEquals(3, cache.getSize());
    assertEquals("This is a log <B>message</B> for revision 123", logMessages.get(0).getMessage());

    cache.add(new LogMessage(126, "This is a log message for revision 126.......")); // punctuation will be trimmed
    assertEquals(4, cache.getSize());

    logMessages = cache.find("message");
    assertEquals(4, logMessages.size());
    assertEquals("This is a log <B>message</B> for revision 126", logMessages.get(3).getMessage());

    cache.add(new LogMessage(127, "<This is a <code>log</code> message for &amp; revision 127.......")); // punctuation will be trimmed
    assertEquals(5, cache.getSize());

    logMessages = cache.find("log");
    assertEquals(5, logMessages.size());
    assertEquals("&lt;This is a &lt;code&gt;<B>log</B>&lt;/code&gt; message for &amp;amp; revision 127", logMessages.get(4).getMessage());

    directory.close();
  }

  public void testAddEmptyAndNull() throws Exception {
    final Directory directory = new RAMDirectory();
    final LogMessageCacheImpl cache = new LogMessageCacheImpl(directory);

    cache.add(new LogMessage(123, ""));
    assertEquals(1, cache.getSize());

    cache.add(new LogMessage(123, null));
    assertEquals(2, cache.getSize());

    directory.close();
  }

}