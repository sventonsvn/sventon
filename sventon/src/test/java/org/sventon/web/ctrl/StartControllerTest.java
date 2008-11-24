package org.sventon.web.ctrl;

import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.sventon.TestUtils;
import org.sventon.appl.Application;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.appl.ConfigDirectory;
import org.sventon.model.RepositoryName;

public class StartControllerTest extends TestCase {

  public void testHandleRequestInternal() throws Exception {
    final ConfigDirectory configDirectory = TestUtils.getTestConfigDirectory();
    configDirectory.setCreateDirectories(false);
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);
    final Application application = new Application(configDirectory, TestUtils.CONFIG_FILE_NAME);

    final StartController ctrl = new StartController();
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
    assertEquals("/repos/test1/browse/", view.getUrl());

    // Configured with two instances
    application.addRepository(createTestRepository("test2"));

    modelAndView = ctrl.handleRequestInternal(mockRequest, null);

    view = (RedirectView) modelAndView.getView();
    assertEquals("/repos/list", view.getUrl());

  }

  private RepositoryConfiguration createTestRepository(final String repositoryName) {
    final RepositoryConfiguration configuration = new RepositoryConfiguration(repositoryName);
    configuration.setRepositoryUrl("http://localhost/svn");
    configuration.setCacheUsed(false);
    configuration.setZippedDownloadsAllowed(false);
    configuration.setEnableAccessControl(false);
    return configuration;
  }

  public void testCreateBrowseUrl() {
    final StartController ctrl = new StartController();
    assertEquals("/repos/test/browse/", ctrl.createBrowseUrl(new RepositoryName("test")));
    assertEquals("/repos/%C3%BC/browse/", ctrl.createBrowseUrl(new RepositoryName("\u00fc")));
  }
}