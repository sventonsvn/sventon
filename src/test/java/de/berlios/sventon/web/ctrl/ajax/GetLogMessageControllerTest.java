package de.berlios.sventon.web.ctrl.ajax;

import de.berlios.sventon.repository.LogMessage;
import de.berlios.sventon.service.RepositoryService;
import de.berlios.sventon.web.command.SVNBaseCommand;
import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.Map;

public class GetLogMessageControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    final RepositoryService mockService = EasyMock.createMock(RepositoryService.class);

    final SVNRevision revision = SVNRevision.create(12);

    final SVNLogEntry logEntry = new SVNLogEntry(null, revision.getNumber(), null, null, "The Message");

    final SVNBaseCommand command = new SVNBaseCommand();
    command.setName("test");

    final GetLogMessageController ctrl = new GetLogMessageController();
    ctrl.setRepositoryService(mockService);

    expect(mockService.getRevision(command.getName(), null, revision.getNumber())).andStubReturn(logEntry);

    replay(mockService);
    final ModelAndView modelAndView = ctrl.svnHandle(null, command, revision, null, null, null, null);
    final Map model = modelAndView.getModel();
    verify(mockService);

    assertEquals(1, model.size());
    final LogMessage logMessage = (LogMessage) model.get("logMessage");
    assertEquals(logEntry.getMessage(), logMessage.getMessage());
    assertEquals(revision.getNumber(), logMessage.getRevision());
    assertEquals("ajax/logMessage", modelAndView.getViewName());
  }
}