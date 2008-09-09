package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.sventon.TestUtils;
import org.sventon.appl.Application;
import org.sventon.model.RepositoryName;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.SVNBaseCommand;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.Map;

public class GoToControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    final RepositoryService mockService = EasyMock.createMock(RepositoryService.class);
    final MockHttpServletRequest mockRequest = new MockHttpServletRequest();

    final SVNBaseCommand command = new SVNBaseCommand();
    command.setName(new RepositoryName("test"));
    command.setPath("/file.txt");
    command.setRevision(SVNRevision.create(12));

    final GoToController ctrl = new GoToController();
    final Application application = TestUtils.getApplicationStub();
    application.setConfigured(true);
    ctrl.setServletContext(new MockServletContext());
    ctrl.setApplication(application);
    ctrl.setRepositoryService(mockService);

    // Test NodeKind.FILE
    expect(mockService.getNodeKind(null, command.getPath(), command.getRevisionNumber())).andStubReturn(SVNNodeKind.FILE);
    replay(mockService);

    ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, mockRequest, null, null);
    Map model = modelAndView.getModel();
    verify(mockService);

    assertEquals(1, model.size());
    RedirectView view = (RedirectView) modelAndView.getView();
    assertEquals("/repos/test/view/file.txt", view.getUrl());

    reset(mockService);
    command.setPath("/dir");

    // Test NodeKind.DIR
    expect(mockService.getNodeKind(null, command.getPath(), command.getRevisionNumber())).andStubReturn(SVNNodeKind.DIR);
    replay(mockService);

    modelAndView = ctrl.svnHandle(null, command, 100, null, mockRequest, null, null);
    model = modelAndView.getModel();
    verify(mockService);

    assertEquals(1, model.size());
    view = (RedirectView) modelAndView.getView();
    assertEquals("/repos/test/browse/dir/", view.getUrl());

    reset(mockService);

    // Test NodeKind.UNKNOWN
    expect(mockService.getNodeKind(null, command.getPath(), command.getRevisionNumber())).andStubReturn(SVNNodeKind.UNKNOWN);
    replay(mockService);

    final BindException errors = new BindException(command, "test");
    assertEquals(0, errors.getAllErrors().size());

    modelAndView = ctrl.svnHandle(null, command, 100, null, mockRequest, null, errors);
    model = modelAndView.getModel();
    verify(mockService);

    assertEquals(4, model.size());
    assertEquals("goto", modelAndView.getViewName());
  }

  public void testCreateBrowseUrl() {
    final GoToController ctrl = new GoToController();
    final SVNBaseCommand cmd = new SVNBaseCommand();
    cmd.setName(new RepositoryName("test"));

    cmd.setPath("/test/dir");
    assertEquals("/repos/test/browse/test/dir/", ctrl.createBrowseUrl(cmd));

    cmd.setPath("/test/dir/");
    assertEquals("/repos/test/browse/test/dir/", ctrl.createBrowseUrl(cmd));
  }

  public void testCreateViewUrl() {
    final GoToController ctrl = new GoToController();
    final SVNBaseCommand cmd = new SVNBaseCommand();
    cmd.setName(new RepositoryName("test"));

    cmd.setPath("/test/file");
    assertEquals("/repos/test/view/test/file", ctrl.createViewUrl(cmd));

    cmd.setPath("/test/file/");
    assertEquals("/repos/test/view/test/file", ctrl.createViewUrl(cmd));
  }

}