package org.sventon.web.ctrl;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.quartz.impl.StdScheduler;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.TestUtils;
import org.sventon.appl.Application;
import org.sventon.appl.ConfigDirectory;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.model.Credentials;

import java.io.File;

public class SubmitConfigurationsControllerTest extends TestCase {

  private ConfigDirectory configDirectory;
  private Application application;

  protected void setUp() throws Exception {
    configDirectory = TestUtils.getTestConfigDirectory();
    configDirectory.setCreateDirectories(false);
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);
    application = new Application(configDirectory, TestUtils.CONFIG_FILE_NAME);
  }

  public void testHandleRequestInternalConfigured() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final SubmitConfigurationsController controller = new SubmitConfigurationsController();
    application.setConfigured(true);
    controller.setApplication(application);
    final ModelAndView modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("error/configurationError", modelAndView.getViewName());
  }

  public void testHandleRequestInternalNoAddedRepository() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final SubmitConfigurationsController controller = new SubmitConfigurationsController();
    application.setConfigured(false);
    controller.setApplication(application);

    final ModelAndView modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("error/configurationError", modelAndView.getViewName());
  }

  public void testHandleRequestInternal() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final SubmitConfigurationsController ctrl = new SubmitConfigurationsController();
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);

    ctrl.setScheduler(new StdScheduler(null, null) {
      public void triggerJob(final String string, final String string1) {
      }
    });

    final RepositoryConfiguration repositoryConfiguration1 = new RepositoryConfiguration("testrepos1");
    repositoryConfiguration1.setRepositoryUrl("http://localhost/1");
    repositoryConfiguration1.setCredentials(new Credentials("user1", "abc123"));
    repositoryConfiguration1.setCacheUsed(false);
    repositoryConfiguration1.setZippedDownloadsAllowed(false);

    final RepositoryConfiguration repositoryConfiguration2 = new RepositoryConfiguration("testrepos2");
    repositoryConfiguration2.setRepositoryUrl("http://localhost/2");
    repositoryConfiguration2.setCredentials(new Credentials("user2", "abc123"));
    repositoryConfiguration2.setCacheUsed(false);
    repositoryConfiguration2.setZippedDownloadsAllowed(false);

    application.addRepository(repositoryConfiguration1);
    application.addRepository(repositoryConfiguration2);
    application.setConfigured(false);
    ctrl.setApplication(application);
    ctrl.setServletContext(new MockServletContext());

    final File configFile1 = new File(configDirectory.getRepositoriesDirectory(), "testrepos1");
    final File configFile2 = new File(configDirectory.getRepositoriesDirectory(), "testrepos2");

    assertFalse(configFile1.exists());
    assertFalse(configFile2.exists());

    final ModelAndView modelAndView = ctrl.handleRequestInternal(request, response);
    assertNotNull(modelAndView);
    assertNull(modelAndView.getViewName()); // Will be null as it is a redirect view.

    //File should now be written
    assertTrue(configFile1.exists());
    assertTrue(configFile2.exists());
    FileUtils.deleteDirectory(configDirectory.getConfigRootDirectory());
    assertFalse(configFile1.exists());
    assertFalse(configFile2.exists());
    assertTrue(application.isConfigured());
  }
}