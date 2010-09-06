package org.sventon.web.ctrl.template;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.TestUtils;
import org.sventon.model.LogEntry;
import org.sventon.model.RepositoryName;
import org.sventon.model.Revision;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.BaseCommand;

import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;

public class ShowRevisionInfoControllerTest {

  @Test
  public void testSvnHandle() throws Exception {
    final RepositoryService mockService = EasyMock.createMock(RepositoryService.class);

    final BaseCommand command = new BaseCommand();
    command.setPath("trunk/test");
    command.setName(new RepositoryName("test"));
    command.setRevision(Revision.create(12));

    final ShowRevisionInfoController ctrl = new ShowRevisionInfoController();
    ctrl.setRepositoryService(mockService);

    expect(mockService.getLogEntry(command.getName(), null, command.getRevisionNumber())).andStubReturn(TestUtils.getLogEntryStub());
    replay(mockService);

    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, null, null, null);
    final Map model = modelAndView.getModel();
    verify(mockService);

    assertEquals(1, model.size());
    final LogEntry revision = (LogEntry) model.get("revisionInfo");
    assertEquals(123, revision.getRevision());
  }
}