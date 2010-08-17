package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.TestUtils;
import org.sventon.model.LogMessageSearchItem;
import org.sventon.model.RepositoryName;
import org.sventon.model.Revision;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.BaseCommand;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

public class GetLogMessageControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    final RepositoryService mockService = EasyMock.createMock(RepositoryService.class);

    final BaseCommand command = new BaseCommand();
    command.setName(new RepositoryName("test"));
    command.setRevision(Revision.create(12));

    final SVNLogEntry svnLogEntry = TestUtils.getLogEntryStub(command.getRevisionNumber());
    final GetLogMessageController ctrl = new GetLogMessageController();
    ctrl.setRepositoryService(mockService);

    expect(mockService.getLogEntry(command.getName(), null, command.getRevisionNumber())).andStubReturn(svnLogEntry);
    replay(mockService);

    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, null, null, null);
    final Map model = modelAndView.getModel();
    verify(mockService);

    assertEquals(1, model.size());
    final LogMessageSearchItem logEntry = (LogMessageSearchItem) model.get("logEntry");
    assertEquals(logEntry.getMessage(), logEntry.getMessage());
    assertEquals(command.getRevisionNumber(), logEntry.getRevision());
  }
}