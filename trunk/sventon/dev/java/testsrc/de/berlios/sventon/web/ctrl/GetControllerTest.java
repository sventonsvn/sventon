package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.appl.Application;
import de.berlios.sventon.repository.SVNRepositoryStub;
import de.berlios.sventon.service.RepositoryServiceImpl;
import de.berlios.sventon.util.ImageUtil;
import de.berlios.sventon.web.command.SVNBaseCommand;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

public class GetControllerTest extends TestCase {

  private static final String TEMPDIR = System.getProperty("java.io.tmpdir");

  public void testSvnHandleGetImageAsInline() throws Exception {
    final Application application = new Application(new File(TEMPDIR), "filename");
    application.setRepositoryService(new RepositoryServiceImpl());
    final SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("/testimage.gif");
    final GetController ctrl = new GetController();
    ctrl.setApplication(application);
    ctrl.setImageUtil(getImageUtil());
    final ModelAndView modelAndView;

    final MockHttpServletRequest req = new MockHttpServletRequest();
    req.addParameter(GetController.DISPLAY_REQUEST_PARAMETER, GetController.DISPLAY_TYPE_INLINE);

    final MockHttpServletResponse res = new MockHttpServletResponse();
    modelAndView = ctrl.svnHandle(new TestRepository(), command, SVNRevision.HEAD, null, req, res, null);

    assertNull(modelAndView);
    assertEquals("image/gif", res.getContentType());
    assertTrue(((String) res.getHeader("Content-disposition")).startsWith("inline"));
  }

  public void testSvnHandleGetFileAsAttachment() throws Exception {
    final Application application = new Application(new File(TEMPDIR), "filename");
    application.setRepositoryService(new RepositoryServiceImpl());
    final SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("/testimage.gif");
    final GetController ctrl = new GetController();
    ctrl.setApplication(application);

    final MockHttpServletRequest req = new MockHttpServletRequest();
    req.addParameter(GetController.DISPLAY_REQUEST_PARAMETER, (String) null);

    final MockHttpServletResponse res = new MockHttpServletResponse();
    final ModelAndView modelAndView = ctrl.svnHandle(new TestRepository(), command, SVNRevision.HEAD, null, req, res, null);

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