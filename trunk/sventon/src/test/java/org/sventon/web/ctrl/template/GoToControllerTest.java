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
import org.sventon.appl.ConfigDirectory;
import org.sventon.model.DirEntry;
import org.sventon.model.RepositoryName;
import org.sventon.model.Revision;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.BaseCommand;

import java.util.Map;

public class GoToControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    final RepositoryService mockService = EasyMock.createMock(RepositoryService.class);
    final MockHttpServletRequest mockRequest = new MockHttpServletRequest();

    final BaseCommand command = new BaseCommand();
    command.setName(new RepositoryName("test"));
    command.setPath("/file.txt");
    command.setRevision(Revision.create(12));

    final GoToController ctrl = new GoToController();

    final ConfigDirectory configDirectory = TestUtils.getTestConfigDirectory();
    configDirectory.setCreateDirectories(false);
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);
    final Application application = new Application(configDirectory);

    application.setConfigured(true);
    ctrl.setServletContext(new MockServletContext());
    ctrl.setApplication(application);
    ctrl.setRepositoryService(mockService);

    // Test NodeKind.FILE
    expect(mockService.getNodeKind(null, command.getPath(), command.getRevisionNumber())).andStubReturn(DirEntry.Kind.FILE);
    replay(mockService);

    ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, mockRequest, null, null);
    Map model = modelAndView.getModel();
    verify(mockService);

    assertEquals(1, model.size());
    RedirectView view = (RedirectView) modelAndView.getView();
    assertEquals("/repos/test/show/file.txt", view.getUrl());

    reset(mockService);
    command.setPath("/dir");

    // Test NodeKind.DIR
    expect(mockService.getNodeKind(null, command.getPath(), command.getRevisionNumber())).andStubReturn(DirEntry.Kind.DIR);
    replay(mockService);

    modelAndView = ctrl.svnHandle(null, command, 100, null, mockRequest, null, null);
    model = modelAndView.getModel();
    verify(mockService);

    assertEquals(1, model.size());
    view = (RedirectView) modelAndView.getView();
    assertEquals("/repos/test/list/dir/", view.getUrl());

    reset(mockService);

    // Test NodeKind.UNKNOWN
    expect(mockService.getNodeKind(null, command.getPath(), command.getRevisionNumber())).andStubReturn(DirEntry.Kind.UNKNOWN);
    replay(mockService);

    final BindException errors = new BindException(command, "test");
    assertEquals(0, errors.getAllErrors().size());

    modelAndView = ctrl.svnHandle(null, command, 100, null, mockRequest, null, errors);
    model = modelAndView.getModel();
    verify(mockService);

    assertEquals(4, model.size());
    assertEquals("goto", modelAndView.getViewName());
  }

}