package org.sventon.web.ctrl.template;

import org.sventon.TestUtils;
import org.sventon.model.RepositoryName;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.SVNBaseCommand;
import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.Map;

public class ShowRevisionInfoControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    final RepositoryService mockService = EasyMock.createMock(RepositoryService.class);

    final SVNBaseCommand command = new SVNBaseCommand();
    command.setPath("trunk/test");
    command.setName(new RepositoryName("test"));
    command.setRevision(SVNRevision.create(12));

    final ShowRevisionInfoController ctrl = new ShowRevisionInfoController();
    ctrl.setRepositoryService(mockService);

    expect(mockService.getRevision(command.getName(), null, command.getRevisionNumber())).andStubReturn(TestUtils.getLogEntryStub());
    replay(mockService);

    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, null, null, null);
    final Map model = modelAndView.getModel();
    verify(mockService);

    assertEquals(1, model.size());
    final SVNLogEntry revision = (SVNLogEntry) model.get("revisionInfo");
    assertEquals(123, revision.getRevision());
  }
}