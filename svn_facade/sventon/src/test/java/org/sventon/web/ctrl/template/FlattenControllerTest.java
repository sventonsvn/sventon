package org.sventon.web.ctrl.template;

import junit.framework.TestCase;

import static org.easymock.EasyMock.expect;

import org.easymock.classextension.EasyMock;

import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.springframework.web.servlet.ModelAndView;
import org.sventon.TestUtils;
import org.sventon.cache.CacheGateway;
import org.sventon.model.*;
import org.sventon.model.DirEntryComparator;
import org.sventon.model.DirEntrySorter;
import org.sventon.web.command.BaseCommand;

import java.util.List;
import java.util.Map;

public class FlattenControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    final CacheGateway mockCache = EasyMock.createMock(CacheGateway.class);

    final List<RepositoryEntry> entries = TestUtils.getFlattenedDirectoriesList();

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
    assertEquals(entries.get(2), ((List<RepositoryEntry>) model.get("svndir")).get(0));
    assertEquals(Boolean.TRUE, model.get("isFlatten"));
  }

}