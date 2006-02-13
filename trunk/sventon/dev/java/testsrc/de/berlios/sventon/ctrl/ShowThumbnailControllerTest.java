package de.berlios.sventon.ctrl;

import de.berlios.sventon.command.SVNBaseCommand;
import de.berlios.sventon.svnsupport.SVNRepositoryStub;
import junit.framework.TestCase;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.mock.web.MockHttpServletRequest;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.List;
import java.util.Map;

public class ShowThumbnailControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    SVNBaseCommand command = new SVNBaseCommand();
    ShowThumbnailController controller = new ShowThumbnailController();
    ModelAndView modelAndView;

    MockHttpServletRequest req = new MockHttpServletRequest();
    String[] pathEntries = new String[]{"file1.gif", "file2.jpg", "file.abc"};
    req.addParameter("entry", pathEntries);

    req.addParameter(GetController.DISPLAY_REQUEST_PARAMETER, GetController.DISPLAY_TYPE_INLINE);

    modelAndView = controller.svnHandle(SVNRepositoryStub.getInstance(), command, SVNRevision.HEAD, req, null, null);

    Map model = modelAndView.getModel();
    List entries = (List) model.get("thumbnailentries");

    assertEquals(2, entries.size());
  }

}