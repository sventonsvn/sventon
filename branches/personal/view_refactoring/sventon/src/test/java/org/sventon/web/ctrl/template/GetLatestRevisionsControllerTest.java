package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.TestUtils;
import org.sventon.model.RepositoryName;
import org.sventon.model.UserRepositoryContext;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.SVNBaseCommand;
import static org.sventon.web.ctrl.template.AbstractSVNTemplateController.FIRST_REVISION;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetLatestRevisionsControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    final UserRepositoryContext userRepositoryContext = new UserRepositoryContext();
    userRepositoryContext.setLatestRevisionsDisplayCount(2);

    final RepositoryService mockService = EasyMock.createMock(RepositoryService.class);

    final GetLatestRevisionsController ctrl = new GetLatestRevisionsController();
    ctrl.setRepositoryService(mockService);

    final SVNBaseCommand command = new SVNBaseCommand();
    command.setName(new RepositoryName("test"));

    final List<SVNLogEntry> revisions = new ArrayList<SVNLogEntry>();
    revisions.add(TestUtils.getLogEntryStub(1));
    revisions.add(TestUtils.getLogEntryStub(2));

    expect(mockService.getRevisions(command.getName(), null, SVNRevision.HEAD.getNumber(), FIRST_REVISION, "/",
        userRepositoryContext.getLatestRevisionsDisplayCount())).andStubReturn(revisions);

    replay(mockService);
    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, userRepositoryContext, null, null, null);
    final Map model = modelAndView.getModel();
    verify(mockService);

    assertEquals(1, model.size());
    assertEquals(revisions, model.get("revisions"));
    assertEquals(2, ((List) model.get("revisions")).size());
    assertEquals("ajax/latestRevisions", modelAndView.getViewName());
  }
}