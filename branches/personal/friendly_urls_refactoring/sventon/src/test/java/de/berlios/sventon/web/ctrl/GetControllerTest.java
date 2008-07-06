package de.berlios.sventon.web.ctrl;

import static de.berlios.sventon.TestUtils.TEMPDIR;
import de.berlios.sventon.appl.Application;
import de.berlios.sventon.repository.SVNRepositoryStub;
import de.berlios.sventon.service.RepositoryServiceImpl;
import de.berlios.sventon.util.WebUtils;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.TestUtils;
import junit.framework.TestCase;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

public class GetControllerTest extends TestCase {

  public void testSvnHandleGetImageAsInline() throws Exception {
    final Application application = TestUtils.getApplicationStub();
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
    modelAndView = ctrl.svnHandle(new TestRepository(), command, 100, null, req, res, null);

    assertNull(modelAndView);
    assertEquals("image/gif", res.getContentType());
    assertTrue(((String) res.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).startsWith("inline"));
  }

  public void testSvnHandleGetFileAsAttachment() throws Exception {
    final Application application = TestUtils.getApplicationStub();
    final SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("/testimage.gif");
    final GetController ctrl = new GetController();
    ctrl.setApplication(application);
    ctrl.setRepositoryService(new RepositoryServiceImpl());

    final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.addParameter(GetController.DISPLAY_REQUEST_PARAMETER, (String) null);

    final MockHttpServletResponse mockResponse = new MockHttpServletResponse();
    final ModelAndView modelAndView = ctrl.svnHandle(new TestRepository(), command, 100, null, mockRequest, mockResponse, null);

    assertNull(modelAndView);

    assertEquals(WebUtils.APPLICATION_OCTET_STREAM, mockResponse.getContentType());
    assertTrue(((String) mockResponse.getHeader(WebUtils.CONTENT_DISPOSITION_HEADER)).startsWith("attachment"));
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