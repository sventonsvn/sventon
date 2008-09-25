package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.appl.Application;
import de.berlios.sventon.repository.SVNRepositoryStub;
import de.berlios.sventon.service.RepositoryServiceImpl;
import de.berlios.sventon.util.WebUtils;
import de.berlios.sventon.web.command.SVNBaseCommand;
import junit.framework.TestCase;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.File;
import java.io.OutputStream;

public class GetControllerTest extends TestCase {

  private static final String TEMPDIR = System.getProperty("java.io.tmpdir");

  public void testSvnHandleGetImageAsInline() throws Exception {
    final Application application = new Application(new File(TEMPDIR), "filename");
    final SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("/testimage.gif");
    final GetController ctrl = new GetController();
    ctrl.setApplication(application);
    ctrl.setRepositoryService(new RepositoryServiceImpl());

    final ConfigurableMimeFileTypeMap mftm = new ConfigurableMimeFileTypeMap();
    mftm.afterPropertiesSet();
    ctrl.setMimeFileTypeMap(mftm);
    final ModelAndView modelAndView;

    final MockHttpServletRequest req = new MockHttpServletRequest();
    req.addParameter(GetController.DISPLAY_REQUEST_PARAMETER, GetController.DISPLAY_TYPE_INLINE);

    final MockHttpServletResponse res = new MockHttpServletResponse();
    modelAndView = ctrl.svnHandle(new TestRepository(), command, SVNRevision.HEAD, null, req, res, null);

    assertNull(modelAndView);
    assertEquals("image/gif", res.getContentType());
    assertTrue(((String) res.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).startsWith("inline"));
  }

  public void testSvnHandleGetFileAsAttachment() throws Exception {
    final Application application = new Application(new File(TEMPDIR), "filename");
    final SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("/testimage.gif");
    final GetController ctrl = new GetController();
    ctrl.setApplication(application);
    ctrl.setRepositoryService(new RepositoryServiceImpl());

    final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.addParameter(GetController.DISPLAY_REQUEST_PARAMETER, (String) null);

    final MockHttpServletResponse mockResponse = new MockHttpServletResponse();
    final ModelAndView modelAndView = ctrl.svnHandle(new TestRepository(), command, SVNRevision.HEAD, null, mockRequest, mockResponse, null);

    assertNull(modelAndView);

    assertEquals(WebUtils.APPLICATION_OCTET_STREAM, mockResponse.getContentType());
    assertTrue(((String) mockResponse.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).startsWith("attachment"));
  }

  static class TestRepository extends SVNRepositoryStub {

    public TestRepository() throws SVNException {
      super(SVNURL.parseURIDecoded("http://localhost/"), null);
    }

    public long getFile(String path, long revision, SVNProperties properties, OutputStream contents) throws SVNException {
      return 0;
    }

  }

}