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
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.TestUtils;
import org.sventon.model.DirEntry;
import org.sventon.model.DirList;
import org.sventon.model.RepositoryName;
import org.sventon.model.Revision;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.BaseCommand;

import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;

public class ListFilesControllerTest {

  @Test
  public void testSvnHandle() throws Exception {
    final RepositoryService mockService = EasyMock.createMock(RepositoryService.class);
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter("rowNumber", "12");

    final List<DirEntry> entries = TestUtils.getFileEntriesDirectoryList();

    final BaseCommand command = new BaseCommand();
    command.setName(new RepositoryName("test"));
    command.setRevision(Revision.create(12));

    final ListFilesController ctrl = new ListFilesController();
    ctrl.setRepositoryService(mockService);

    expect(mockService.list(null, command.getPath(), command.getRevisionNumber())).andStubReturn(new DirList(entries, null));
    replay(mockService);

    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, request, null, null);
    final Map model = modelAndView.getModel();
    verify(mockService);

    assertEquals(3, model.size());
    assertEquals(12, model.get("rowNumber"));
    assertEquals(entries.get(0), ((List<DirEntry>) model.get("dirEntries")).get(0));
  }
}