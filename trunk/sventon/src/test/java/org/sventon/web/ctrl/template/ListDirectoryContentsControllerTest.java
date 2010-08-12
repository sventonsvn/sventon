package org.sventon.web.ctrl.template;

import junit.framework.TestCase;

import static org.easymock.EasyMock.expect;

import org.easymock.classextension.EasyMock;

import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

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

public class ListDirectoryContentsControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    final RepositoryService mockService = EasyMock.createMock(RepositoryService.class);
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter("rowNumber", "12");

    final List<DirEntry> entries = TestUtils.getFileEntriesDirectoryList();

    final BaseCommand command = new BaseCommand();
    command.setName(new RepositoryName("test"));
    command.setRevision(Revision.create(12));

    final ListDirectoryContentsController ctrl = new ListDirectoryContentsController();
    ctrl.setRepositoryService(mockService);

    expect(mockService.list(null, command.getPath(), command.getRevisionNumber())).andStubReturn(new DirList(entries, null));
    replay(mockService);

    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, request, null, null);
    final Map model = modelAndView.getModel();
    verify(mockService);

    assertEquals(2, model.size());
    assertEquals(entries.get(0), ((List<DirEntry>) model.get("svndir")).get(0));
    assertNull(modelAndView.getViewName());
  }

}