/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
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
import org.sventon.cache.CacheGateway;
import org.sventon.model.LogMessageSearchItem;
import org.sventon.model.RepositoryName;
import org.sventon.model.Revision;
import org.sventon.web.command.BaseCommand;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.sventon.web.ctrl.template.SearchLogsController.SEARCH_STRING_PARAMETER;
import static org.sventon.web.ctrl.template.SearchLogsController.START_DIR_PARAMETER;

public class SearchLogsControllerTest {

  @Test
  public void testSvnHandle() throws Exception {
    final CacheGateway mockCache = EasyMock.createMock(CacheGateway.class);
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter(SEARCH_STRING_PARAMETER, "abc");
    request.addParameter(START_DIR_PARAMETER, "/trunk/");

    final BaseCommand command = new BaseCommand();
    command.setPath("trunk/test");
    command.setName(new RepositoryName("test"));
    command.setRevision(Revision.create(12));

    final SearchLogsController ctrl = new SearchLogsController();
    ctrl.setCacheGateway(mockCache);

    final List<LogMessageSearchItem> result = new ArrayList<LogMessageSearchItem>();
    result.add(new LogMessageSearchItem(TestUtils.createLogEntry(123, "jesper", new Date(), "Revision 123.", null)));

    expect(mockCache.find(command.getName(), "abc", "/trunk/")).andStubReturn(result);
    replay(mockCache);

    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, request, null, null);
    final Map model = modelAndView.getModel();
    verify(mockCache);

    assertEquals(4, model.size());
    assertEquals("abc", model.get(SEARCH_STRING_PARAMETER));
    assertEquals("/trunk/", model.get(START_DIR_PARAMETER));
    assertEquals(result, model.get("logEntries"));
    assertTrue((Boolean) model.get("isLogSearch"));
  }
}