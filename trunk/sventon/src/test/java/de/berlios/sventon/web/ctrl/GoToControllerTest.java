package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.service.RepositoryService;
import de.berlios.sventon.web.command.SVNBaseCommand;
import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.Map;

public class GoToControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    final RepositoryService mockService = EasyMock.createMock(RepositoryService.class);
    final MockHttpServletRequest mockRequest = new MockHttpServletRequest();

    final SVNRevision revision = SVNRevision.create(12);
    final SVNBaseCommand command = new SVNBaseCommand();
    command.setName("test");
    command.setPath("/file.txt");
    command.setRevision(String.valueOf(revision.getNumber()));

    final GoToController ctrl = new GoToController();
    ctrl.setRepositoryService(mockService);

    // Test NodeKind.FILE
    EasyMock.expect(mockService.getNodeKind(null, command.getPath(), revision.getNumber())).andStubReturn(SVNNodeKind.FILE);
    replay(mockService);

    ModelAndView modelAndView = ctrl.svnHandle(null, command, revision, null, mockRequest, null, null);
    Map model = modelAndView.getModel();
    verify(mockService);

    assertEquals(0, model.size());
    RedirectView view = (RedirectView) modelAndView.getView();
    assertEquals("showfile.svn?path=/file.txt&revision=12&name=test", view.getUrl());

    reset(mockService);
    command.setPath("/dir");

    // Test NodeKind.DIR
    expect(mockService.getNodeKind(null, command.getPath(), revision.getNumber())).andStubReturn(SVNNodeKind.DIR);
    replay(mockService);

    modelAndView = ctrl.svnHandle(null, command, revision, null, mockRequest, null, null);
    model = modelAndView.getModel();
    verify(mockService);

    assertEquals(0, model.size());
    view = (RedirectView) modelAndView.getView();
    assertEquals("repobrowser.svn?path=/dir&revision=12&name=test", view.getUrl());
  }

}