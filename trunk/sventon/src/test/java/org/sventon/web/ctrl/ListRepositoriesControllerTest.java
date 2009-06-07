package org.sventon.web.ctrl;

import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.sventon.TestUtils;
import org.sventon.appl.Application;
import org.sventon.appl.ConfigDirectory;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.model.Credentials;
import org.sventon.model.RepositoryName;
import org.sventon.model.UserContext;
import org.sventon.model.UserRepositoryContext;

public class ListRepositoriesControllerTest extends TestCase {

  private Application application;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  protected void setUp() throws Exception {
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    ConfigDirectory configDirectory = TestUtils.getTestConfigDirectory();
    configDirectory.setCreateDirectories(false);
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);
    application = new Application(configDirectory, TestUtils.CONFIG_FILE_NAME);
  }

  public void testHandleRequestInternal() throws Exception {
    final ListRepositoriesController ctrl = new ListRepositoriesController();
    ctrl.setServletContext(new MockServletContext());
    ctrl.setApplication(application);

    ModelAndView modelAndView = ctrl.handleRequestInternal(request, response);

    // Not configured
    assertTrue(modelAndView.getView() instanceof RedirectView);

    application.addRepository(createTestRepository("test1"));
    application.addRepository(createTestRepository("test2"));
    application.setConfigured(true);

    modelAndView = ctrl.handleRequestInternal(request, response);
    assertEquals("listRepositories", modelAndView.getViewName());
  }

  public void testHandleRequestInternalLogout() throws Exception {
    //Create a mock session and prepare it. After the contrller call completes, the session should be empty.
    final MockHttpSession session = new MockHttpSession();
    final UserRepositoryContext context1 = new UserRepositoryContext();
    context1.setCredentials(new Credentials("UID1", "PWD1"));
    final UserRepositoryContext context2 = new UserRepositoryContext();
    context2.setCredentials(new Credentials("UID2", "PWD2"));
    final UserContext userContext = new UserContext();

    final RepositoryName repo1 = new RepositoryName("repo1");
    final RepositoryName repo2 = new RepositoryName("repo2");

    userContext.add(repo1, context1);
    userContext.add(repo2, context2);

    session.putValue("userContext", userContext);
    request.setSession(session);

    final ListRepositoriesController controller = new ListRepositoriesController();
    application.addRepository(createTestRepository("test1"));
    application.addRepository(createTestRepository("test2"));
    application.setConfigured(true);
    controller.setApplication(application);

    ModelAndView modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("listRepositories", modelAndView.getViewName());
    assertSame(userContext, session.getAttribute("userContext"));
    UserContext uCFromSession = (UserContext) session.getAttribute("userContext");
    UserRepositoryContext uRC1FromSession = uCFromSession.getUserRepositoryContext(repo1);
    assertEquals("UID1", uRC1FromSession.getCredentials().getUserName());
    assertEquals("PWD1", uRC1FromSession.getCredentials().getPassword());

    UserRepositoryContext uRC2FromSession = uCFromSession.getUserRepositoryContext(repo2);
    assertEquals("UID2", uRC2FromSession.getCredentials().getUserName());
    assertEquals("PWD2", uRC2FromSession.getCredentials().getPassword());

    //Now try again, this time with an incorrect repository name
    request.setParameter("logout", "true");
    request.setParameter("repositoryName", "repoWRONG");
    modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("listRepositories", modelAndView.getViewName());
    assertSame(userContext, session.getAttribute("userContext"));
    uCFromSession = (UserContext) session.getAttribute("userContext");
    uRC1FromSession = uCFromSession.getUserRepositoryContext(repo1);
    assertEquals("UID1", uRC1FromSession.getCredentials().getUserName());
    assertEquals("PWD1", uRC1FromSession.getCredentials().getPassword());

    uRC2FromSession = uCFromSession.getUserRepositoryContext(repo2);
    assertEquals("UID2", uRC2FromSession.getCredentials().getUserName());
    assertEquals("PWD2", uRC2FromSession.getCredentials().getPassword());

    //Now try again, this time with no repository name
    request.setParameter("logout", "true");
    modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("listRepositories", modelAndView.getViewName());
    assertSame(userContext, session.getAttribute("userContext"));
    uCFromSession = (UserContext) session.getAttribute("userContext");
    uRC1FromSession = uCFromSession.getUserRepositoryContext(repo1);
    assertEquals("UID1", uRC1FromSession.getCredentials().getUserName());
    assertEquals("PWD1", uRC1FromSession.getCredentials().getPassword());

    uRC2FromSession = uCFromSession.getUserRepositoryContext(repo2);
    assertEquals("UID2", uRC2FromSession.getCredentials().getUserName());
    assertEquals("PWD2", uRC2FromSession.getCredentials().getPassword());

    //Now try again, this time with no logout param
    request.setParameter("logout", "");
    request.setParameter("repositoryName", "repo1");
    modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("listRepositories", modelAndView.getViewName());
    assertSame(userContext, session.getAttribute("userContext"));
    uCFromSession = (UserContext) session.getAttribute("userContext");
    uRC1FromSession = uCFromSession.getUserRepositoryContext(repo1);
    assertEquals("UID1", uRC1FromSession.getCredentials().getUserName());
    assertEquals("PWD1", uRC1FromSession.getCredentials().getPassword());

    uRC2FromSession = uCFromSession.getUserRepositoryContext(repo2);
    assertEquals("UID2", uRC2FromSession.getCredentials().getUserName());
    assertEquals("PWD2", uRC2FromSession.getCredentials().getPassword());

    //Now try again, this time supply correct logout param and repo name
    request.setParameter("logout", "true");
    request.setParameter("repositoryName", "repo1");
    modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("listRepositories", modelAndView.getViewName());

    assertSame(userContext, session.getAttribute("userContext"));
    uCFromSession = (UserContext) session.getAttribute("userContext");
    uRC1FromSession = uCFromSession.getUserRepositoryContext(repo1);
    assertSame(Credentials.EMPTY, uRC1FromSession.getCredentials());

    uRC2FromSession = uCFromSession.getUserRepositoryContext(repo2);
    assertEquals("UID2", uRC2FromSession.getCredentials().getUserName());
    assertEquals("PWD2", uRC2FromSession.getCredentials().getPassword());
  }

  public void testHandleRequestInternal2() throws Exception {
    final ListRepositoriesController ctrl = new ListRepositoriesController();
    ctrl.setServletContext(new MockServletContext());
    ctrl.setApplication(application);

    final MockHttpServletRequest mockRequest = new MockHttpServletRequest();

    // Not configured
    application.setConfigured(false);
    ModelAndView modelAndView = ctrl.handleRequestInternal(mockRequest, null);

    RedirectView view = (RedirectView) modelAndView.getView();
    assertEquals("/repos/listconfigs", view.getUrl());

    // Configured - but without added instances (should not be possible)
    application.setConfigured(true);
    try {
      ctrl.handleRequestInternal(mockRequest, null);
      fail("Should throw IllegalStateException ");
    } catch (IllegalStateException e) {
      // expected
    }

    // Configured with one instance
    application.addRepository(createTestRepository("test1"));

    modelAndView = ctrl.handleRequestInternal(mockRequest, null);

    view = (RedirectView) modelAndView.getView();
    assertEquals("/repos/test1/list/", view.getUrl());

    // Configured with two instances
    application.addRepository(createTestRepository("test2"));

    modelAndView = ctrl.handleRequestInternal(mockRequest, null);
    assertEquals("listRepositories", modelAndView.getViewName());
  }

  private RepositoryConfiguration createTestRepository(final String repositoryName) {
    final RepositoryConfiguration configuration = new RepositoryConfiguration(repositoryName);
    configuration.setRepositoryUrl("http://localhost/svn");
    configuration.setCacheUsed(false);
    configuration.setZippedDownloadsAllowed(false);
    configuration.setEnableAccessControl(false);
    return configuration;
  }

  public void testCreateListUrl() {
    final ListRepositoriesController ctrl = new ListRepositoriesController();
    assertEquals("/repos/test/list/", ctrl.createListUrl(new RepositoryName("test")));
    assertEquals("/repos/%C3%BC/list/", ctrl.createListUrl(new RepositoryName("\u00fc")));
  }
}