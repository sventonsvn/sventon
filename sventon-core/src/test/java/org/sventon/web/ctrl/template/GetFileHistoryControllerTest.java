package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.model.LogEntry;
import org.sventon.model.PathRevision;
import org.sventon.model.RepositoryName;
import org.sventon.model.Revision;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.BaseCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

public class GetFileHistoryControllerTest extends TestCase {

  private final List<LogEntry> logEntries = new ArrayList<LogEntry>();
  private final BaseCommand command = new BaseCommand();
  private RepositoryService mockService;
  private MockHttpServletRequest request;
  private GetFileHistoryController ctrl;
  private RepositoryName repositoryName;

  protected void setUp() throws Exception {
    logEntries.add(new LogEntry(3, null , null));
    logEntries.add(new LogEntry(2, null , null));
    logEntries.add(new LogEntry(1, null , null));

    repositoryName = new RepositoryName("test");
    command.setPath("/trunk/test");
    command.setName(repositoryName);
    command.setRevision(Revision.create(12));

    mockService = EasyMock.createMock(RepositoryService.class);
    request = new MockHttpServletRequest();
    ctrl = new GetFileHistoryController();
    ctrl.setRepositoryService(mockService);
  }

  protected void tearDown() throws Exception {
    EasyMock.reset(mockService);
  }

  private Map executeTest() throws Exception {
    expect(mockService.getLogEntries(repositoryName, null, command.getRevisionNumber(), 1,  command.getPath(), 100, false, true)).andStubReturn(logEntries);
    replay(mockService);
    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, request, null, null);
    verify(mockService);
    return modelAndView.getModel();
  }

  public void testGetFileHistoryController() throws Exception {
    final Map model = executeTest();
    assertEquals(2, model.size());
    assertTrue(command.getRevision().getNumber() == (Long) model.get("currentRevision"));
    final List<LogEntry> logEntries = (List<LogEntry>) model.get("logEntries");
    assertEquals(logEntries.size(), this.logEntries.size());
    assertEquals(3, logEntries.get(0).getRevision());
  }

  public void testGetFileHistoryControllerArchivedEntry() throws Exception {
    request.setParameter(GetFileHistoryController.ARCHIVED_ENTRY, "zippedTestFile");
    final Map model = executeTest();
    assertEquals(3, model.size());
    assertTrue(command.getRevision().getNumber() == (Long) model.get("currentRevision"));
    final List<LogEntry> logEntries = (List<LogEntry>) model.get("logEntries");
    assertEquals(logEntries.size(), this.logEntries.size());
    assertEquals(3, logEntries.get(0).getRevision());
    assertEquals("zippedTestFile", model.get(GetFileHistoryController.ARCHIVED_ENTRY));
  }

}