package de.berlios.sventon.web.ctrl.ajax;

import de.berlios.sventon.service.RepositoryService;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.UserRepositoryContext;
import de.berlios.sventon.appl.RepositoryName;
import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.SVNLogEntry;

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
    revisions.add(new SVNLogEntry(null, 1, null, null, null));
    revisions.add(new SVNLogEntry(null, 2, null, null, null));

    expect(mockService.getRevisions(command.getName(), null, -1, 1, "/",
        userRepositoryContext.getLatestRevisionsDisplayCount())).andStubReturn(revisions);

    replay(mockService);
    final ModelAndView modelAndView = ctrl.svnHandle(null, command, null, userRepositoryContext, null, null, null);
    final Map model = modelAndView.getModel();
    verify(mockService);

    assertEquals(1, model.size());
    assertSame(revisions, model.get("revisions"));
    assertEquals("ajax/latestRevisions", modelAndView.getViewName());
  }
}