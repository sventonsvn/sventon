package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.appl.Application;
import de.berlios.sventon.appl.RepositoryConfiguration;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;

public class StartControllerTest extends TestCase {

  private static final String TEMPDIR = System.getProperty("java.io.tmpdir");

  public void testHandleRequestInternal() throws Exception {
    final Application application = new Application(new File(TEMPDIR), "filename");
    final StartController ctrl = new StartController();
    ctrl.setApplication(application);

    final MockHttpServletRequest mockRequest = new MockHttpServletRequest();

    // Not configured
    application.setConfigured(false);
    ModelAndView modelAndView = ctrl.handleRequestInternal(mockRequest, null);

    RedirectView view = (RedirectView) modelAndView.getView();
    assertEquals("config.svn", view.getUrl());

    // Configured - but without added instances (should not be possible)
    application.setConfigured(true);
    try {
      ctrl.handleRequestInternal(mockRequest, null);
      fail("Should throw IllegalStateException ");
    } catch (IllegalStateException e) {
      // expected
    }

    // Configured with one instance
    application.addInstance(createTestInstance("test1"));

    modelAndView = ctrl.handleRequestInternal(mockRequest, null);

    view = (RedirectView) modelAndView.getView();
    assertEquals("repobrowser.svn?name=test1", view.getUrl());

    // Configured with two instances
    application.addInstance(createTestInstance("test2"));

    modelAndView = ctrl.handleRequestInternal(mockRequest, null);

    view = (RedirectView) modelAndView.getView();
    assertEquals("listinstances.svn", view.getUrl());

  }

  private RepositoryConfiguration createTestInstance(final String instanceName) {
    final RepositoryConfiguration configuration = new RepositoryConfiguration(instanceName);
    configuration.setRepositoryUrl("http://localhost/svn");
    configuration.setCacheUsed(false);
    configuration.setZippedDownloadsAllowed(false);
    configuration.setEnableAccessControl(false);
    return configuration;
  }
}