package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.appl.RepositoryName;
import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.service.RepositoryService;
import de.berlios.sventon.web.command.SVNBaseCommand;
import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListDirectoryContentsControllerTest extends TestCase {

  public void testSvnHandle() throws Exception {
    final RepositoryService mockService = EasyMock.createMock(RepositoryService.class);
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter("rowNumber", "12");

    final List<RepositoryEntry> entries = new ArrayList<RepositoryEntry>();
    entries.add(new RepositoryEntry(new SVNDirEntry(null, "test.jpg", SVNNodeKind.FILE, 0, false, 0, null, null), "/"));

    final SVNBaseCommand command = new SVNBaseCommand();
    command.setName(new RepositoryName("test"));
    command.setRevision(SVNRevision.create(12));

    final ListDirectoryContentsController ctrl = new ListDirectoryContentsController();
    ctrl.setRepositoryService(mockService);

    expect(mockService.list(null, command.getPath(), command.getRevisionNumber(), new HashMap())).andStubReturn(entries);
    replay(mockService);

    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, request, null, null);
    final Map model = modelAndView.getModel();
    verify(mockService);

    assertEquals(2, model.size());
    assertEquals(entries.get(0), ((List<RepositoryEntry>) model.get("svndir")).get(0));
    assertNull(modelAndView.getViewName());
  }

}