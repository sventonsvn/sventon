package de.berlios.sventon.repository.cache.commitmessagecache;

import de.berlios.sventon.repository.CommitMessage;
import junit.framework.TestCase;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class CommitMessageCacheTest extends TestCase {

  public void testAdd() throws Exception {
    final Directory directory = new RAMDirectory();

    //final String tempDir = System.getProperty("java.io.tmpdir") + "sventonCacheTest";
    //final Directory directory = FSDirectory.getDirectory(tempDir, true);

    final CommitMessageCacheImpl cache = new CommitMessageCacheImpl(directory);

    cache.add(new CommitMessage(123, "This is a commit message for revision 123."));
    assertEquals(1, cache.getSize());

    assertEquals(1, cache.find("123").size());

    cache.add(new CommitMessage(124, "This is a commit message for revision 124."));
    assertEquals(2, cache.getSize());

    assertEquals(2, cache.find("message").size());
    cache.add(new CommitMessage(125, "This is a commit message for revision 125."));
    assertEquals(3, cache.getSize());

    cache.add(new CommitMessage(126, "This is a commit message for revision 126."));
    assertEquals(4, cache.getSize());

    assertEquals(4, cache.find("message").size());

    directory.close();
  }

  public void testAddEmptyAndNull() throws Exception {
    final Directory directory = new RAMDirectory();
    final CommitMessageCacheImpl cache = new CommitMessageCacheImpl(directory);

    cache.add(new CommitMessage(123, "this is"));
    assertEquals(1, cache.getSize());

    directory.close();
  }

}