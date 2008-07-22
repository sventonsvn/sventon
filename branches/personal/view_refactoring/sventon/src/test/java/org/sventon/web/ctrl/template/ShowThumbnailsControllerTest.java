package org.sventon.web.ctrl.template;

import org.sventon.SVNRepositoryStub;
import org.sventon.web.command.SVNBaseCommand;
import org.sventon.web.ctrl.template.GetController;
import junit.framework.TestCase;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNFileRevision;

import java.util.List;
import java.util.Map;

public class ShowThumbnailsControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    final SVNBaseCommand command = new SVNBaseCommand();
    final ShowThumbnailsController ctrl = new ShowThumbnailsController();

    final ConfigurableMimeFileTypeMap mftm = new ConfigurableMimeFileTypeMap();
    mftm.afterPropertiesSet();
    ctrl.setMimeFileTypeMap(mftm);

    final MockHttpServletRequest req = new MockHttpServletRequest();
    final String[] pathEntries = new String[]{
        "file1.gif;;123",
        "file2.jpg;;123",
        "file.abc;;123"};
    req.addParameter("entry", pathEntries);

    req.addParameter(GetController.DISPLAY_REQUEST_PARAMETER, GetController.DISPLAY_TYPE_INLINE);

    final ModelAndView modelAndView = ctrl.svnHandle(new TestRepository(),
        command, 100, null, req, null, null);

    final Map model = modelAndView.getModel();
    final List entries = (List) model.get("thumbnailentries");

    assertEquals(2, entries.size());

    final SVNFileRevision entry0 = (SVNFileRevision) entries.get(0);
    assertEquals("file1.gif", entry0.getPath());
    assertEquals(123, entry0.getRevision());
  }

  static class TestRepository extends SVNRepositoryStub {
    public TestRepository() throws SVNException {
      super(SVNURL.parseURIDecoded("http://localhost/"), null);
    }
  }

}