package de.berlios.sventon.ctrl;

import de.berlios.sventon.command.SVNBaseCommand;
import de.berlios.sventon.index.RevisionIndexer;
import de.berlios.sventon.svnsupport.SVNRepositoryStub;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.List;
import java.util.Map;
import java.io.File;

public class SearchControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    SVNBaseCommand command = new SVNBaseCommand();
    SearchController ctrl = new SearchController();
    ModelAndView modelAndView;

    MockHttpServletRequest req = new MockHttpServletRequest();
    req.addParameter("searchString", "file1");
    req.addParameter("startDir", "");

    SVNRepository repos = SVNRepositoryStub.getInstance();
    RevisionIndexer indexer = new RevisionIndexer(repos);
    RepositoryConfiguration config = new RepositoryConfiguration();
    config.setRepositoryRoot(repos.getLocation().toString());
    config.setSVNConfigurationPath(System.getProperty("java.io.tmpdir"));
    indexer.setRepositoryConfiguration(config);
    ctrl.setRevisionIndexer(indexer);
    modelAndView = ctrl.svnHandle(SVNRepositoryStub.getInstance(), command, SVNRevision.HEAD, req, null, null);

    Map model = modelAndView.getModel();
    List entries = (List) model.get("svndir");

    assertTrue(Boolean.valueOf(model.get("isSearch").toString()));
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
    req.addParameter("searchString", "file1");
    req.addParameter("startDir", "/dir1");

    SVNRepository repos = SVNRepositoryStub.getInstance();
    RevisionIndexer indexer = new RevisionIndexer(repos);
    RepositoryConfiguration config = new RepositoryConfiguration();
    config.setRepositoryRoot(repos.getLocation().toString());
    config.setSVNConfigurationPath(System.getProperty("java.io.tmpdir"));
    indexer.setRepositoryConfiguration(config);
    ctrl.setRevisionIndexer(indexer);
    modelAndView = ctrl.svnHandle(SVNRepositoryStub.getInstance(), command, SVNRevision.HEAD, req, null, null);

    Map model = modelAndView.getModel();
    List entries = (List) model.get("svndir");

    assertTrue(Boolean.valueOf(model.get("isSearch").toString()));
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

  protected void tearDown() throws Exception {
    File tempIndex = new File(System.getProperty("java.io.tmpdir") + "/" + RevisionIndexer.INDEX_FILENAME);
    if (tempIndex.exists()) {
      tempIndex.delete();
    }
  }


}
