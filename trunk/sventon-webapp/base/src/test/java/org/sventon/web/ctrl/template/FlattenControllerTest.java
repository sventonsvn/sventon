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
import org.springframework.web.servlet.ModelAndView;
import org.sventon.TestUtils;
import org.sventon.cache.CacheGateway;
import org.sventon.model.*;
import org.sventon.web.command.BaseCommand;

import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;

public class FlattenControllerTest {

  @Test
  public void testSvnHandle() throws Exception {
    final CacheGateway mockCache = EasyMock.createMock(CacheGateway.class);

    final List<DirEntry> entries = TestUtils.getFlattenedDirectoriesList();

    final BaseCommand command = new BaseCommand();
    command.setName(new RepositoryName("test"));
    command.setRevision(Revision.create(12));

    final UserRepositoryContext context = new UserRepositoryContext();
    context.setSortMode(DirEntrySorter.SortMode.DESC);
    context.setSortType(DirEntryComparator.SortType.REVISION);

    final FlattenController ctrl = new FlattenController();
    ctrl.setCacheGateway(mockCache);

    expect(mockCache.findDirectories(command.getName(), command.getPath())).andStubReturn(entries);
    replay(mockCache);

    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, context, null, null, null);
    final Map model = modelAndView.getModel();
    verify(mockCache);

    assertEquals(2, model.size());
    assertEquals(entries.get(2), ((List<DirEntry>) model.get("dirEntries")).get(0));
    assertEquals(Boolean.TRUE, model.get("isFlatten"));
  }

}