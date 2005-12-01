package de.berlios.sventon.index;

import de.berlios.sventon.svnsupport.SVNRepositoryStub;
import junit.framework.TestCase;

public class RevisionIndexerTest extends TestCase {

  private RevisionIndexer indexer = null;

  public void setUp() throws Exception {
    indexer = new RevisionIndexer(SVNRepositoryStub.getInstance());
    indexer.populateIndex();
    assertEquals(8, indexer.getIndexCount());
    //indexer.dumpIndex();
  }

  public void testUpdate() throws Exception {
    assertEquals(8, indexer.getIndexCount());
    indexer.updateIndex();
    assertEquals(7, indexer.getIndexCount());
  }

  public void testFind() throws Exception {
    assertEquals(2, indexer.find("html", "/").size());
  }

  public void testFindMixedCase() throws Exception {
    assertEquals(2, indexer.find("hTmL", "/").size());
  }

  public void testFindPattern() throws Exception {
    assertEquals(7, indexer.findPattern(".*[12].*", "/").size());
  }

  public void testFindPatternCamelCase() throws Exception {
    assertEquals(2, indexer.findPattern(".*/[D][a-z0-9]+[F][a-z0-9]+.*", "/").size());
  }

  public void testGetDirectories() throws Exception {
    assertEquals(2, indexer.getDirectories("/").size());
    assertEquals(1, indexer.getDirectories("/dir1/").size());
  }

  public void testGetDirectoriesMixedCase() throws Exception {
    assertEquals(2, indexer.getDirectories("/").size());
    assertEquals(0, indexer.getDirectories("/DIR1/").size());
  }

}