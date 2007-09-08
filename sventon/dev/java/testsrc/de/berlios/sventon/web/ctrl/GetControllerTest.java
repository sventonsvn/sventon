package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.repository.SVNRepositoryStub;
import de.berlios.sventon.util.ImageUtil;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.service.RepositoryServiceImpl;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

public class GetControllerTest extends TestCase {

  public void testSvnHandleGetImageAsInline() throws Exception {
    SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("/testimage.gif");
    GetController ctrl = new GetController();
    ctrl.setImageUtil(getImageUtil());
    ctrl.setRepositoryService(new RepositoryServiceImpl());
    ModelAndView modelAndView;

    MockHttpServletRequest req = new MockHttpServletRequest();
    req.addParameter(GetController.DISPLAY_REQUEST_PARAMETER, GetController.DISPLAY_TYPE_INLINE);

    MockHttpServletResponse res = new MockHttpServletResponse();
    modelAndView = ctrl.svnHandle(new TestRepository(), command, SVNRevision.HEAD, null, req, res, null);

    assertNull(modelAndView);
    assertEquals("image/gif", res.getContentType());
    assertTrue(((String) res.getHeader("Content-disposition")).startsWith("inline"));
  }

  public void testSvnHandleGetFileAsAttachment() throws Exception {
    SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("/testimage.gif");
    GetController ctrl = new GetController();
    ctrl.setRepositoryService(new RepositoryServiceImpl());
    ModelAndView modelAndView;

    MockHttpServletRequest req = new MockHttpServletRequest();
    req.addParameter(GetController.DISPLAY_REQUEST_PARAMETER, (String) null);

    MockHttpServletResponse res = new MockHttpServletResponse();
    modelAndView = ctrl.svnHandle(new TestRepository(), command, SVNRevision.HEAD, null, req, res, null);

    assertNull(modelAndView);

    assertEquals("application/octet-stream", res.getContentType());
    assertTrue(((String) res.getHeader("Content-disposition")).startsWith("attachment"));
  }

  private ImageUtil getImageUtil() {
    ImageUtil imageUtil = new ImageUtil();
    Properties prop = new Properties();
    prop.setProperty("jpg", "image/jpg");
    prop.setProperty("jpe", "image/jpg");
    prop.setProperty("jpeg", "image/jpg");
    prop.setProperty("gif", "image/gif");
    prop.setProperty("png", "image/png");
    imageUtil.setMimeMappings(prop);
    return imageUtil;
  }

  static class TestRepository extends SVNRepositoryStub {

    public TestRepository() throws SVNException {
      super(SVNURL.parseURIDecoded("http://localhost/"), null);
    }

    public long getFile(String path, long revision, Map properties, OutputStream contents) throws SVNException {
      return 0;
    }

  }

}