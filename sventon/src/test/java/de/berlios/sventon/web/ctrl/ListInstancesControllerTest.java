package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.appl.Application;
import de.berlios.sventon.appl.RepositoryName;
import de.berlios.sventon.web.model.UserContext;
import de.berlios.sventon.web.model.UserRepositoryContext;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;

public class ListInstancesControllerTest extends TestCase {

  private static final String TEMPDIR = System.getProperty("java.io.tmpdir");

  public void testHandleRequestInternal() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();

    final ListRepositoriesController controller = new ListRepositoriesController();
    final Application application = new Application(new File(TEMPDIR), "filename");
    controller.setApplication(application);

    ModelAndView modelAndView = controller.handleRequestInternal(request, response);

    // Not configured
    assertTrue(modelAndView.getView() instanceof RedirectView);

    application.setConfigured(true);

    modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("listInstances", modelAndView.getViewName());
  }

  public void testHandleRequestInternalLogout() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();

    //Create a mock session and prepare it. After the contrller call completes, the session should be empty.
    final MockHttpSession session = new MockHttpSession();
    final UserRepositoryContext context1 = new UserRepositoryContext();
    context1.setUid("UID1");
    context1.setPwd("PWD1");
    final UserRepositoryContext context2 = new UserRepositoryContext();
    context2.setUid("UID2");
    context2.setPwd("PWD2");
    final UserContext userContext = new UserContext();

    final RepositoryName repo1 = new RepositoryName("repo1");
    final RepositoryName repo2 = new RepositoryName("repo2");

    userContext.add(repo1, context1);
    userContext.add(repo2, context2);

    session.putValue("userContext", userContext);
    request.setSession(session);

    final ListRepositoriesController controller = new ListRepositoriesController();
    final Application application = new Application(new File(TEMPDIR), "filename");
    application.setConfigured(true);
    controller.setApplication(application);

    ModelAndView modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("listInstances", modelAndView.getViewName());
    assertSame(userContext, session.getAttribute("userContext"));
    UserContext uCFromSession = (UserContext) session.getAttribute("userContext");
    UserRepositoryContext uRC1FromSession = uCFromSession.getRepositoryContext(repo1);
    assertEquals("UID1", uRC1FromSession.getUid());
    assertEquals("PWD1", uRC1FromSession.getPwd());

    UserRepositoryContext uRC2FromSession = uCFromSession.getRepositoryContext(repo2);
    assertEquals("UID2", uRC2FromSession.getUid());
    assertEquals("PWD2", uRC2FromSession.getPwd());

    //Now try again, this time with an incorrect repository name
    request.setParameter("logout", "true");
    request.setParameter("repositoryName", "repoWRONG");
    modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("listInstances", modelAndView.getViewName());
    assertSame(userContext, session.getAttribute("userContext"));
    uCFromSession = (UserContext) session.getAttribute("userContext");
    uRC1FromSession = uCFromSession.getRepositoryContext(repo1);
    assertEquals("UID1", uRC1FromSession.getUid());
    assertEquals("PWD1", uRC1FromSession.getPwd());

    uRC2FromSession = uCFromSession.getRepositoryContext(repo2);
    assertEquals("UID2", uRC2FromSession.getUid());
    assertEquals("PWD2", uRC2FromSession.getPwd());

    //Now try again, this time with no repository name
    request.setParameter("logout", "true");
    modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("listInstances", modelAndView.getViewName());
    assertSame(userContext, session.getAttribute("userContext"));
    uCFromSession = (UserContext) session.getAttribute("userContext");
    uRC1FromSession = uCFromSession.getRepositoryContext(repo1);
    assertEquals("UID1", uRC1FromSession.getUid());
    assertEquals("PWD1", uRC1FromSession.getPwd());

    uRC2FromSession = uCFromSession.getRepositoryContext(repo2);
    assertEquals("UID2", uRC2FromSession.getUid());
    assertEquals("PWD2", uRC2FromSession.getPwd());

    //Now try again, this time with no logout param
    request.setParameter("logout", "");
    request.setParameter("repositoryName", "repo1");
    modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("listInstances", modelAndView.getViewName());
    assertSame(userContext, session.getAttribute("userContext"));
    uCFromSession = (UserContext) session.getAttribute("userContext");
    uRC1FromSession = uCFromSession.getRepositoryContext(repo1);
    assertEquals("UID1", uRC1FromSession.getUid());
    assertEquals("PWD1", uRC1FromSession.getPwd());

    uRC2FromSession = uCFromSession.getRepositoryContext(repo2);
    assertEquals("UID2", uRC2FromSession.getUid());
    assertEquals("PWD2", uRC2FromSession.getPwd());

    //Now try again, this time supply correct logout param and repo name
    request.setParameter("logout", "true");
    request.setParameter("repositoryName", "repo1");
    modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("listInstances", modelAndView.getViewName());

    assertSame(userContext, session.getAttribute("userContext"));
    uCFromSession = (UserContext) session.getAttribute("userContext");
    uRC1FromSession = uCFromSession.getRepositoryContext(repo1);
    assertNull(uRC1FromSession.getUid());
    assertNull(uRC1FromSession.getPwd());

    uRC2FromSession = uCFromSession.getRepositoryContext(repo2);
    assertEquals("UID2", uRC2FromSession.getUid());
    assertEquals("PWD2", uRC2FromSession.getPwd());

  }
}