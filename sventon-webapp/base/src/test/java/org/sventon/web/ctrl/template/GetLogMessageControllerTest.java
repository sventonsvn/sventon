/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.ctrl.template;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.TestUtils;
import org.sventon.model.LogEntry;
import org.sventon.model.LogMessageSearchItem;
import org.sventon.model.RepositoryName;
import org.sventon.model.Revision;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.BaseCommand;

import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;

public class GetLogMessageControllerTest {

  @Test
  public void testSvnHandle() throws Exception {
    final RepositoryService mockService = EasyMock.createMock(RepositoryService.class);

    final BaseCommand command = new BaseCommand();
    command.setName(new RepositoryName("test"));
    command.setRevision(Revision.create(12));

    final LogEntry svnLogEntry = TestUtils.getLogEntryStub(command.getRevisionNumber());
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