package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.model.PathRevision;
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

public class GetFileHistoryControllerTest extends TestCase {

  private final List<PathRevision> pathRevisions = new ArrayList<PathRevision>();
  private final BaseCommand command = new BaseCommand();
  private RepositoryService mockService;
  private MockHttpServletRequest request;
  private GetFileHistoryController ctrl;

  protected void setUp() throws Exception {
    pathRevisions.add(new PathRevision("/trunk/test", Revision.create(1)));
    pathRevisions.add(new PathRevision("/trunk/test", Revision.create(2)));
    pathRevisions.add(new PathRevision("/trunk/test", Revision.create(3)));

    command.setPath("/trunk/test");
    command.setName(new RepositoryName("test"));
    command.setRevision(Revision.create(12));

    mockService = EasyMock.createMock(RepositoryService.class);
    request = new MockHttpServletRequest();
    ctrl = new GetFileHistoryController();
    ctrl.setRepositoryService(mockService);
  }

  protected void tearDown() throws Exception {
    EasyMock.reset(mockService);
  }

  private Map executeTest() throws Exception {
    expect(mockService.getFileRevisions(null, command.getPath(), command.getRevisionNumber())).andStubReturn(pathRevisions);
    replay(mockService);
    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, null, request, null, null);
    verify(mockService);
    return modelAndView.getModel();
  }

  public void testGetFileHistoryController() throws Exception {
    final Map model = executeTest();
    assertEquals(2, model.size());
    assertTrue(command.getRevision().getNumber() == (Long) model.get("currentRevision"));
    final List<PathRevision> fileRevisionsInModel = (List<PathRevision>) model.get("fileRevisions");
    assertEquals(fileRevisionsInModel.size(), pathRevisions.size());
    assertEquals(3, fileRevisionsInModel.get(0).getRevision().getNumber());
  }

  public void testGetFileHistoryControllerArchivedEntry() throws Exception {
    request.setParameter(GetFileHistoryController.ARCHIVED_ENTRY, "zippedTestFile");
    final Map model = executeTest();
    assertEquals(3, model.size());
    assertTrue(command.getRevision().getNumber() == (Long) model.get("currentRevision"));
    final List<PathRevision> fileRevisionsInModel = (List<PathRevision>) model.get("fileRevisions");
    assertEquals(fileRevisionsInModel.size(), pathRevisions.size());
    assertEquals(3, fileRevisionsInModel.get(0).getRevision().getNumber());
    assertEquals("zippedTestFile", model.get(GetFileHistoryController.ARCHIVED_ENTRY));
  }

}