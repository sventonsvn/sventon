package de.berlios.sventon.ctrl;

import de.berlios.sventon.command.SVNBaseCommand;
import de.berlios.sventon.index.RevisionIndexer;
import de.berlios.sventon.svnsupport.SVNRepositoryStub;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.List;
import java.util.Map;

public class SearchControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    SVNBaseCommand command = new SVNBaseCommand();
    SearchController ctrl = new SearchController();
    ModelAndView modelAndView;

    MockHttpServletRequest req = new MockHttpServletRequest();
    req.addParameter("sventonSearchString", "file1");
    req.addParameter("startDir", "");

    ctrl.setRevisionIndexer(new RevisionIndexer(SVNRepositoryStub.getInstance()));
    modelAndView = ctrl.svnHandle(SVNRepositoryStub.getInstance(), command, SVNRevision.HEAD, req, null, null);

    Map model = modelAndView.getModel();
    List entries = (List) model.get("svndir");

    assertTrue(new Boolean(model.get("isSearch").toString()).booleanValue());
    assertEquals(2, entries.size());

    assertEquals(RepositoryEntry.Kind.file, ((RepositoryEntry) entries.get(0)).getKind());

    req = new MockHttpServletRequest();
    try {
      modelAndView = ctrl.svnHandle(SVNRepositoryStub.getInstance(), command, SVNRevision.HEAD, req, null, null);
      fail("Should throw NPE");
    } catch (NullPointerException ex) {
      // expected
    }
  }

  public void testSvnHandleII() throws Exception {
    SVNBaseCommand command = new SVNBaseCommand();
    SearchController ctrl = new SearchController();
    ModelAndView modelAndView;

    MockHttpServletRequest req = new MockHttpServletRequest();
    req.addParameter("sventonSearchString", "file1");
    req.addParameter("startDir", "/dir1");

    ctrl.setRevisionIndexer(new RevisionIndexer(SVNRepositoryStub.getInstance()));
    modelAndView = ctrl.svnHandle(SVNRepositoryStub.getInstance(), command, SVNRevision.HEAD, req, null, null);

    Map model = modelAndView.getModel();
    List entries = (List) model.get("svndir");

    assertTrue(new Boolean(model.get("isSearch").toString()).booleanValue());
    assertEquals(1, entries.size());

    assertEquals(RepositoryEntry.Kind.file, ((RepositoryEntry) entries.get(0)).getKind());

    req = new MockHttpServletRequest();
    try {
      modelAndView = ctrl.svnHandle(SVNRepositoryStub.getInstance(), command, SVNRevision.HEAD, req, null, null);
      fail("Should throw NPE");
    } catch (NullPointerException ex) {
      // expected
    }
  }

}