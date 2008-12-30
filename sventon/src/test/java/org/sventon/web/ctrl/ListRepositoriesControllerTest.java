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
import org.sventon.model.Credentials;
import org.sventon.model.RepositoryName;
import org.sventon.model.UserContext;
import org.sventon.model.UserRepositoryContext;

public class ListRepositoriesControllerTest extends TestCase {

  private Application application;

  protected void setUp() throws Exception {
    ConfigDirectory configDirectory = TestUtils.getTestConfigDirectory();
    configDirectory.setCreateDirectories(false);
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);
    application = new Application(configDirectory, TestUtils.CONFIG_FILE_NAME);
  }

  public void testHandleRequestInternal() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();

    final ListRepositoriesController ctrl = new ListRepositoriesController();
    ctrl.setServletContext(new MockServletContext());
    ctrl.setApplication(application);

    ModelAndView modelAndView = ctrl.handleRequestInternal(request, response);

    // Not configured
    assertTrue(modelAndView.getView() instanceof RedirectView);

    application.setConfigured(true);

    modelAndView = ctrl.handleRequestInternal(request, response);
    assertEquals("listRepositories", modelAndView.getViewName());
  }

  public void testHandleRequestInternalLogout() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();

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
    application.setConfigured(true);
    controller.setApplication(application);

    ModelAndView modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("listRepositories", modelAndView.getViewName());
    assertSame(userContext, session.getAttribute("userContext"));
    UserContext uCFromSession = (UserContext) session.getAttribute("userContext");
    UserRepositoryContext uRC1FromSession = uCFromSession.getUserRepositoryContext(repo1);
    assertEquals("UID1", uRC1FromSession.getCredentials().getUsername());
    assertEquals("PWD1", uRC1FromSession.getCredentials().getPassword());

    UserRepositoryContext uRC2FromSession = uCFromSession.getUserRepositoryContext(repo2);
    assertEquals("UID2", uRC2FromSession.getCredentials().getUsername());
    assertEquals("PWD2", uRC2FromSession.getCredentials().getPassword());

    //Now try again, this time with an incorrect repository name
    request.setParameter("logout", "true");
    request.setParameter("repositoryName", "repoWRONG");
    modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("listRepositories", modelAndView.getViewName());
    assertSame(userContext, session.getAttribute("userContext"));
    uCFromSession = (UserContext) session.getAttribute("userContext");
    uRC1FromSession = uCFromSession.getUserRepositoryContext(repo1);
    assertEquals("UID1", uRC1FromSession.getCredentials().getUsername());
    assertEquals("PWD1", uRC1FromSession.getCredentials().getPassword());

    uRC2FromSession = uCFromSession.getUserRepositoryContext(repo2);
    assertEquals("UID2", uRC2FromSession.getCredentials().getUsername());
    assertEquals("PWD2", uRC2FromSession.getCredentials().getPassword());

    //Now try again, this time with no repository name
    request.setParameter("logout", "true");
    modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("listRepositories", modelAndView.getViewName());
    assertSame(userContext, session.getAttribute("userContext"));
    uCFromSession = (UserContext) session.getAttribute("userContext");
    uRC1FromSession = uCFromSession.getUserRepositoryContext(repo1);
    assertEquals("UID1", uRC1FromSession.getCredentials().getUsername());
    assertEquals("PWD1", uRC1FromSession.getCredentials().getPassword());

    uRC2FromSession = uCFromSession.getUserRepositoryContext(repo2);
    assertEquals("UID2", uRC2FromSession.getCredentials().getUsername());
    assertEquals("PWD2", uRC2FromSession.getCredentials().getPassword());

    //Now try again, this time with no logout param
    request.setParameter("logout", "");
    request.setParameter("repositoryName", "repo1");
    modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("listRepositories", modelAndView.getViewName());
    assertSame(userContext, session.getAttribute("userContext"));
    uCFromSession = (UserContext) session.getAttribute("userContext");
    uRC1FromSession = uCFromSession.getUserRepositoryContext(repo1);
    assertEquals("UID1", uRC1FromSession.getCredentials().getUsername());
    assertEquals("PWD1", uRC1FromSession.getCredentials().getPassword());

    uRC2FromSession = uCFromSession.getUserRepositoryContext(repo2);
    assertEquals("UID2", uRC2FromSession.getCredentials().getUsername());
    assertEquals("PWD2", uRC2FromSession.getCredentials().getPassword());

    //Now try again, this time supply correct logout param and repo name
    request.setParameter("logout", "true");
    request.setParameter("repositoryName", "repo1");
    modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("listRepositories", modelAndView.getViewName());

    assertSame(userContext, session.getAttribute("userContext"));
    uCFromSession = (UserContext) session.getAttribute("userContext");
    uRC1FromSession = uCFromSession.getUserRepositoryContext(repo1);
    assertNull(uRC1FromSession.getCredentials());

    uRC2FromSession = uCFromSession.getUserRepositoryContext(repo2);
    assertEquals("UID2", uRC2FromSession.getCredentials().getUsername());
    assertEquals("PWD2", uRC2FromSession.getCredentials().getPassword());
  }
}