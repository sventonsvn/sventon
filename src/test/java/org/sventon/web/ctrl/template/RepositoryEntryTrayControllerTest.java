package org.sventon.web.ctrl.template;

import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.TestUtils;
import org.sventon.appl.Application;
import org.sventon.appl.ConfigDirectory;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.model.RepositoryEntry;
import org.sventon.model.RepositoryName;
import org.sventon.model.UserRepositoryContext;
import org.sventon.service.RepositoryService;
import org.sventon.web.command.SVNBaseCommand;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.Map;

public class RepositoryEntryTrayControllerTest extends TestCase {

  private final SVNBaseCommand command = new SVNBaseCommand();
  private RepositoryService mockService;
  private MockHttpServletRequest request;
  private RepositoryEntryTrayController ctrl;
  private RepositoryEntry entry;
  private UserRepositoryContext context;

  protected void setUp() throws Exception {
    final ConfigDirectory configDirectory = TestUtils.getTestConfigDirectory();
    configDirectory.setCreateDirectories(false);
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);
    final Application application = new Application(configDirectory, TestUtils.CONFIG_FILE_NAME);

    final RepositoryConfiguration configuration = new RepositoryConfiguration("test");
    configuration.setEnableEntryTray(true);
    application.addRepository(configuration);

    entry = new RepositoryEntry(new SVNDirEntry(null, null, "/trunk/test", SVNNodeKind.FILE, 0, false, 0, null, null), "/");

    command.setPath("/trunk/test");
    command.setName(new RepositoryName("test"));
    command.setRevision(SVNRevision.create(10));

    mockService = EasyMock.createMock(RepositoryService.class);
    request = new MockHttpServletRequest();
    ctrl = new RepositoryEntryTrayController();
    ctrl.setRepositoryService(mockService);
    ctrl.setApplication(application);
    context = new UserRepositoryContext();
  }

  protected void tearDown() throws Exception {
    EasyMock.reset(mockService);
  }

  private Map executeTest() throws Exception {
    expect(mockService.getEntryInfo(null, command.getPath(), command.getRevisionNumber())).andStubReturn(entry);
    replay(mockService);
    final ModelAndView modelAndView = ctrl.svnHandle(null, command, 100, context, request, null, null);
    verify(mockService);
    return modelAndView.getModel();
  }

  public void testAddAndRemove() throws Exception {
    request.setParameter("action", RepositoryEntryTrayController.PARAMETER_ADD);
    request.setParameter("pegrev", "10");
    assertEquals(0, context.getRepositoryEntryTray().getSize());

    Map model = executeTest();

    assertEquals(1, model.size());
    assertTrue(10 == (Long) model.get("pegrev"));
    assertEquals(1, context.getRepositoryEntryTray().getSize());

    EasyMock.reset(mockService);

    request.setParameter("action", RepositoryEntryTrayController.PARAMETER_REMOVE);
    request.setParameter("pegrev", "10");

    model = executeTest();

    assertEquals(1, model.size());
    assertTrue(10 == (Long) model.get("pegrev"));
    assertEquals(0, context.getRepositoryEntryTray().getSize());
  }

}