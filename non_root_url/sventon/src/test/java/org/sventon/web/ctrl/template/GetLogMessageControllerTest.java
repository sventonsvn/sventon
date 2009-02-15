package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.TestUtils;
import org.sventon.model.LogMessage;
import org.sventon.model.RepositoryName;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.BaseCommand;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.Map;

public class GetLogMessageControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    final RepositoryService mockService = EasyMock.createMock(RepositoryService.class);

    final BaseCommand command = new BaseCommand();
    command.setName(new RepositoryName("test"));
    command.setRevision(SVNRevision.create(12));

    final SVNLogEntry logEntry = TestUtils.getLogEntryStub(command.getRevisionNumber());
    final GetLogMessageController ctrl = new GetLogMessageController();
    ctrl.setRepositoryService(mockService);

    expect(mockService.getRevision(command.getName(), null,
        command.getRevisionNumber())).andStubReturn(logEntry);
    replay(mockService);

    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, null, null, null);
    final Map model = modelAndView.getModel();
    verify(mockService);

    assertEquals(1, model.size());
    final LogMessage logMessage = (LogMessage) model.get("logMessage");
    assertEquals(logEntry.getMessage(), logMessage.getMessage());
    assertEquals(command.getRevisionNumber(), logMessage.getRevision());
  }
}