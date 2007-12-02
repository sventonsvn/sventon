package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.appl.Application;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
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
    assertEquals("listinstances", modelAndView.getViewName());
  }
}