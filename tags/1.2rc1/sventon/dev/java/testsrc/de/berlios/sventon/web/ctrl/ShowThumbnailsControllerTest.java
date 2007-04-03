package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.repository.SVNRepositoryStub;
import de.berlios.sventon.util.ImageUtil;
import de.berlios.sventon.web.command.SVNBaseCommand;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ShowThumbnailsControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    SVNBaseCommand command = new SVNBaseCommand();
    ShowThumbnailsController controller = new ShowThumbnailsController();

    final Properties mimeMappings = new Properties();
    mimeMappings.setProperty("jpg", "image/jpg");
    mimeMappings.setProperty("jpe", "image/jpg");
    mimeMappings.setProperty("jpeg", "image/jpg");
    mimeMappings.setProperty("gif", "image/gif");
    mimeMappings.setProperty("png", "image/png");

    final ImageUtil imageUtil = new ImageUtil();
    imageUtil.setMimeMappings(mimeMappings);
    controller.setImageUtil(imageUtil);

    MockHttpServletRequest req = new MockHttpServletRequest();
    String[] pathEntries = new String[]{"file1.gif", "file2.jpg", "file.abc"};
    req.addParameter("entry", pathEntries);

    req.addParameter(GetController.DISPLAY_REQUEST_PARAMETER, GetController.DISPLAY_TYPE_INLINE);

    final ModelAndView modelAndView = controller.svnHandle(new TestRepository(),
        command, SVNRevision.HEAD, null, req, null, null);

    Map model = modelAndView.getModel();
    List entries = (List) model.get("thumbnailentries");

    assertEquals(2, entries.size());
  }

  static class TestRepository extends SVNRepositoryStub {
    public TestRepository() throws SVNException {
      super(SVNURL.parseURIDecoded("http://localhost/"), null);
    }
  }

}