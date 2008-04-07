package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.appl.RepositoryName;
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

    final SVNBaseCommand command = new SVNBaseCommand();
    command.setName(new RepositoryName("test"));
    command.setPath("/file.txt");
    command.setRevision(SVNRevision.create(12));

    final GoToController ctrl = new GoToController();
    ctrl.setRepositoryService(mockService);

    // Test NodeKind.FILE
    expect(mockService.getNodeKind(null, command.getPath(), command.getRevisionNumber())).andStubReturn(SVNNodeKind.FILE);
    replay(mockService);

    ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, mockRequest, null, null);
    Map model = modelAndView.getModel();
    verify(mockService);

    assertEquals(3, model.size());
    RedirectView view = (RedirectView) modelAndView.getView();
    assertEquals("showfile.svn", view.getUrl());

    reset(mockService);
    command.setPath("/dir");

    // Test NodeKind.DIR
    expect(mockService.getNodeKind(null, command.getPath(), command.getRevisionNumber())).andStubReturn(SVNNodeKind.DIR);
    replay(mockService);

    modelAndView = ctrl.svnHandle(null, command, 100, null, mockRequest, null, null);
    model = modelAndView.getModel();
    verify(mockService);

    assertEquals(3, model.size());
    view = (RedirectView) modelAndView.getView();
    assertEquals("repobrowser.svn", view.getUrl());
  }

}