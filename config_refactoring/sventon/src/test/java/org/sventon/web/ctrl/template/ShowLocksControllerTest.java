package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.TestUtils;
import org.sventon.model.RepositoryName;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.SVNBaseCommand;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.Collection;
import java.util.Map;

public class ShowLocksControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    final RepositoryService mockService = EasyMock.createMock(RepositoryService.class);

    final SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("trunk/test/");
    command.setName(new RepositoryName("test"));
    command.setRevision(SVNRevision.create(12));

    final ShowLocksController ctrl = new ShowLocksController();
    ctrl.setRepositoryService(mockService);

    expect(mockService.getLocks(null, command.getPath())).andStubReturn(TestUtils.getLocksStub(command.getPath()));
    replay(mockService);

    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, null, null, null);
    final Map model = modelAndView.getModel();
    verify(mockService);

    assertEquals(1, model.size());
    final Collection locks = (Collection) model.get("currentLocks");
    assertEquals(1, locks.size());
  }
}