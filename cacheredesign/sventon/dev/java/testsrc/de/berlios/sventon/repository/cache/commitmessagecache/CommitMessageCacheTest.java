package de.berlios.sventon.repository.cache.commitmessagecache;

import de.berlios.sventon.repository.CommitMessage;
import junit.framework.TestCase;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.util.List;

public class CommitMessageCacheTest extends TestCase {

  public void testAdd() throws Exception {
    final Directory directory = new RAMDirectory();

    //final String tempDir = System.getProperty("java.io.tmpdir") + "sventonCacheTest";
    //final Directory directory = FSDirectory.getDirectory(tempDir, true);

    final CommitMessageCacheImpl cache = new CommitMessageCacheImpl(directory);

    cache.add(new CommitMessage(123, "This is a commit message for revision 123."));

    List<CommitMessage> commitMessages = cache.find("123");
    assertEquals(1, commitMessages.size());
    assertEquals("This is a commit message for revision <B>123</B>", commitMessages.get(0).getMessage());

    cache.add(new CommitMessage(124, "This is a commit message for revision 124."));
    assertEquals(2, cache.getSize());

    commitMessages = cache.find("message");
    assertEquals(2, commitMessages.size());
    cache.add(new CommitMessage(125, "This is a commit message for revision 125."));
    assertEquals(3, cache.getSize());
    assertEquals("This is a commit <B>message</B> for revision 123", commitMessages.get(0).getMessage());

    cache.add(new CommitMessage(126, "This is a commit message for revision 126.......")); // punctuation will be trimmed
    assertEquals(4, cache.getSize());

    commitMessages = cache.find("message");
    assertEquals(4, commitMessages.size());
    assertEquals("This is a commit <B>message</B> for revision 126", commitMessages.get(3).getMessage());

    cache.add(new CommitMessage(127, "<This is a <code>commit</code> message for &amp; revision 127.......")); // punctuation will be trimmed
    assertEquals(5, cache.getSize());

    commitMessages = cache.find("commit");
    assertEquals(5, commitMessages.size());
    assertEquals("&lt;This is a &lt;code&gt;<B>commit</B>&lt;/code&gt; message for &amp;amp; revision 127", commitMessages.get(4).getMessage());

    directory.close();
  }

  public void testAddEmptyAndNull() throws Exception {
    final Directory directory = new RAMDirectory();
    final CommitMessageCacheImpl cache = new CommitMessageCacheImpl(directory);

    cache.add(new CommitMessage(123, ""));
    assertEquals(1, cache.getSize());

    cache.add(new CommitMessage(123, null));
    assertEquals(2, cache.getSize());

    directory.close();
  }

}