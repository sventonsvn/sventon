package de.berlios.sventon.ctrl;

import de.berlios.sventon.command.SVNBaseCommand;
import de.berlios.sventon.index.RevisionIndexer;
import de.berlios.sventon.svnsupport.SVNRepositoryStub;
import junit.framework.TestCase;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.List;
import java.util.Map;
import java.io.File;

public class FlattenControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    SVNBaseCommand command = new SVNBaseCommand();
    FlattenController controller = new FlattenController();
    ModelAndView modelAndView;

    SVNRepository repos = SVNRepositoryStub.getInstance();
    RevisionIndexer indexer = new RevisionIndexer(repos);
    RepositoryConfiguration config = new RepositoryConfiguration();
    config.setRepositoryRoot(repos.getLocation().toString());
    config.setSVNConfigurationPath(System.getProperty("java.io.tmpdir"));
    indexer.setRepositoryConfiguration(config);
    controller.setRevisionIndexer(indexer);
    modelAndView = controller.svnHandle(SVNRepositoryStub.getInstance(), command, SVNRevision.HEAD, null, null, null);

    Map model = modelAndView.getModel();
    List entries = (List) model.get("svndir");

    assertTrue(Boolean.valueOf(model.get("isFlatten").toString()));
    assertEquals(2, entries.size());

    assertEquals(RepositoryEntry.Kind.dir, ((RepositoryEntry) entries.get(0)).getKind());
    assertEquals("dir1", ((RepositoryEntry) entries.get(0)).getName());

    assertEquals(RepositoryEntry.Kind.dir, ((RepositoryEntry) entries.get(1)).getKind());
    assertEquals("dir2", ((RepositoryEntry) entries.get(1)).getName());
  }

  protected void tearDown() throws Exception {
    File tempIndex = new File(System.getProperty("java.io.tmpdir") + "/" + RevisionIndexer.INDEX_FILENAME);
    if (tempIndex.exists()) {
      tempIndex.delete();
    }
  }
}
