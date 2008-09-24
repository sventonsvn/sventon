package org.sventon.web.ctrl.template;

import org.sventon.TestUtils;
import org.sventon.model.RepositoryName;
import org.sventon.model.RepositoryEntry;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.SVNBaseCommand;
import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListFilesControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    final RepositoryService mockService = EasyMock.createMock(RepositoryService.class);
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter("rowNumber", "12");

    final List<RepositoryEntry> entries = TestUtils.getFileEntriesDirectoryList();

    final SVNBaseCommand command = new SVNBaseCommand();
    command.setName(new RepositoryName("test"));
    command.setRevision(SVNRevision.create(12));

    final ListFilesController ctrl = new ListFilesController();
    ctrl.setRepositoryService(mockService);

    expect(mockService.list(null, command.getPath(), command.getRevisionNumber(), new HashMap())).andStubReturn(entries);
    replay(mockService);

    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, request, null, null);
    final Map model = modelAndView.getModel();
    verify(mockService);

    assertEquals(3, model.size());
    assertEquals(12, model.get("rowNumber"));
    assertEquals(entries.get(0), ((List<RepositoryEntry>) model.get("svndir")).get(0));
  }
}