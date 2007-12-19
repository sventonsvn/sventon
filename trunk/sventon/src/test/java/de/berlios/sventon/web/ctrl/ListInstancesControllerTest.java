package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.appl.Application;
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

    final ListInstancesController controller = new ListInstancesController();
    final Application application = new Application(new File(TEMPDIR), "filename");
    controller.setApplication(application);

    ModelAndView modelAndView = null;
    modelAndView = controller.handleRequestInternal(request, response);

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
    final UserRepositoryContext context = new UserRepositoryContext();
    session.putValue("userRepositoryContext", context);
    request.setSession(session);

    final ListInstancesController controller = new ListInstancesController();
    final Application application = new Application(new File(TEMPDIR), "filename");
    application.setConfigured(true);
    controller.setApplication(application);

    ModelAndView modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("listInstances", modelAndView.getViewName());
    assertSame(context, session.getAttribute("userRepositoryContext"));

    //Now try again, this time supply logout param
    request.setParameter("logout", "true");
    modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("listInstances", modelAndView.getViewName());
    assertNull(session.getAttribute("userRepositoryContext"));

  }
}