package de.berlios.sventon.ctrl;

import de.berlios.sventon.command.SVNBaseCommand;
import de.berlios.sventon.index.RevisionIndexer;
import de.berlios.sventon.svnsupport.SVNRepositoryStub;
import junit.framework.TestCase;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.List;
import java.util.Map;

public class FlattenControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    SVNBaseCommand command = new SVNBaseCommand();
    FlattenController controller = new FlattenController();
    ModelAndView modelAndView;

    controller.setRevisionIndexer(new RevisionIndexer(SVNRepositoryStub.getInstance()));
    modelAndView = controller.svnHandle(SVNRepositoryStub.getInstance(), command, SVNRevision.HEAD, null, null, null);

    Map model = modelAndView.getModel();
    List entries = (List) model.get("svndir");

    assertTrue(new Boolean(model.get("isFlatten").toString()).booleanValue());
    assertEquals(2, entries.size());

    assertEquals("dir", ((RepositoryEntry) entries.get(0)).getKind());
    assertEquals("dir1", ((RepositoryEntry) entries.get(0)).getName());

    assertEquals("dir", ((RepositoryEntry) entries.get(1)).getKind());
    assertEquals("dir2", ((RepositoryEntry) entries.get(1)).getName());
  }

}
