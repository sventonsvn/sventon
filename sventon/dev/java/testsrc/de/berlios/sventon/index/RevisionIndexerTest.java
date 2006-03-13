package de.berlios.sventon.index;

import de.berlios.sventon.ctrl.RepositoryConfiguration;
import de.berlios.sventon.repository.SVNRepositoryStub;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.io.File;
import java.util.List;

public class RevisionIndexerTest extends TestCase {

  private RevisionIndexer indexer = null;

  public void setUp() throws Exception {
    SVNRepository repos = SVNRepositoryStub.getInstance();
    RepositoryConfiguration config = new RepositoryConfiguration();
    config.setSVNConfigurationPath(System.getProperty("java.io.tmpdir"));
    config.setRepositoryRoot(repos.getLocation().toString());
    indexer = new RevisionIndexer(repos);
    indexer.setRepositoryConfiguration(config);
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
    List list = indexer.find("html", "/");
    System.out.println("list = " + list);
    indexer.dumpIndex();
    assertEquals(2, list.size());
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
    assertEquals(2, indexer.findDirectories("/").size());
    assertEquals(1, indexer.findDirectories("/dir1/").size());
  }

  public void testGetDirectoriesMixedCase() throws Exception {
    assertEquals(2, indexer.findDirectories("/").size());
    assertEquals(0, indexer.findDirectories("/DIR1/").size());
  }

  protected void tearDown() throws Exception {
    File tempIndex = new File(System.getProperty("java.io.tmpdir") + "/" + RevisionIndexer.INDEX_FILENAME);
    if (tempIndex.exists()) {
      tempIndex.delete();
    }
  }
}
