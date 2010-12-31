/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.model.LogEntry;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetFileHistoryControllerTest {

  private final List<LogEntry> logEntries = new ArrayList<LogEntry>();
  private final BaseCommand command = new BaseCommand();
  private RepositoryService mockService;
  private MockHttpServletRequest request;
  private GetFileHistoryController ctrl;
  private RepositoryName repositoryName;

  @Before
  public void setUp() throws Exception {
    logEntries.add(new LogEntry(3, null, null));
    logEntries.add(new LogEntry(2, null, null));
    logEntries.add(new LogEntry(1, null, null));

    repositoryName = new RepositoryName("test");
    command.setPath("/trunk/test");
    command.setName(repositoryName);
    command.setRevision(Revision.create(12));

    mockService = EasyMock.createMock(RepositoryService.class);
    request = new MockHttpServletRequest();
    ctrl = new GetFileHistoryController();
    ctrl.setRepositoryService(mockService);
  }

  @After
  public void tearDown() throws Exception {
    EasyMock.reset(mockService);
  }

  private Map executeTest() throws Exception {
    expect(mockService.getLogEntries(repositoryName, null, command.getRevisionNumber(), 1, command.getPath(), 100, false, true)).andStubReturn(logEntries);
    replay(mockService);
    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, request, null, null);
    verify(mockService);
    return modelAndView.getModel();
  }

  @Test
  public void testGetFileHistoryController() throws Exception {
    final Map model = executeTest();
    assertEquals(2, model.size());
    assertTrue(command.getRevision().getNumber() == (Long) model.get("currentRevision"));
    final List<LogEntry> logEntries = (List<LogEntry>) model.get("logEntries");
    assertEquals(logEntries.size(), this.logEntries.size());
    assertEquals(3, logEntries.get(0).getRevision());
  }

  @Test
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