package org.sventon.web.ctrl;

import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.TestUtils;
import org.sventon.appl.Application;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.model.RepositoryName;
import org.sventon.web.command.ConfigCommand;
import static org.sventon.web.command.ConfigCommand.AccessMethod.USER;

import java.util.Map;
import java.util.Set;

public class ConfigurationFormControllerTest extends TestCase {

  public void testShowForm() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest("GET", "");
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationFormController ctrl = new ConfigurationFormController();
    ctrl.setCommandClass(ConfigCommand.class);
    ctrl.setApplication(TestUtils.getApplicationStub());
    final ModelAndView modelAndView = ctrl.handleRequest(request, response);
    assertNotNull(modelAndView);
    assertEquals(3, modelAndView.getModel().size());
  }

  //Test what happens if a repository is partially configured and the config view is invoked
  //this could happen if one started to configure sventon and then called 'browse'.
  public void testShowFormConfiguredII() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest("GET", "");
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationFormController ctrl = new ConfigurationFormController();

    final RepositoryConfiguration repositoryConfig = new RepositoryConfiguration("repository1");
    final Application application = TestUtils.getApplicationStub();
    application.setConfigured(false);
    application.addRepository(repositoryConfig);
    ctrl.setCommandClass(ConfigCommand.class);
    ctrl.setApplication(application);
    final ModelAndView modelAndView = ctrl.handleRequest(request, response);
    assertNotNull(modelAndView);
    assertEquals(3, modelAndView.getModel().size());
  }

  public void testProcessFormSubmissionNonConfigured() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationFormController ctrl = new ConfigurationFormController();
    final Application application = TestUtils.getApplicationStub();
    assertEquals(0, application.getRepositoryCount());
    assertFalse(application.isConfigured());
    ctrl.setApplication(application);
    final ConfigCommand command = new ConfigCommand();
    final String repositoryName = "testrepos";
    command.setName(repositoryName);
    command.setRepositoryUrl("http://localhost");
    final BindException exception = new BindException(command, "test");
    final ModelAndView modelAndView = ctrl.onSubmit(request, response, command, exception);
    assertNotNull(modelAndView);
    assertEquals(4, modelAndView.getModel().size());
    assertEquals(1, application.getRepositoryCount());
    assertFalse(application.isConfigured());
    final Map model = modelAndView.getModel();
    assertEquals(new RepositoryName(repositoryName), ((Set) model.get("addedRepositories")).iterator().next());
  }

  public void testProcessFormSubmissionNonConfiguredUserBasedAccessControl() throws Exception {
    final String repositoryName = "testrepos";

    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationFormController ctrl = new ConfigurationFormController();
    final Application application = TestUtils.getApplicationStub();
    assertEquals(0, application.getRepositoryCount());
    assertFalse(application.isConfigured());
    ctrl.setApplication(application);
    final ConfigCommand command = new ConfigCommand();
    command.setName(repositoryName);
    command.setRepositoryUrl("http://localhost");
    command.setAccessMethod(USER);
    command.setZippedDownloadsAllowed(true);
    command.setConnectionTestUid("test uid");
    command.setConnectionTestPwd("test pwd");
    final BindException exception = new BindException(command, "test");
    final ModelAndView modelAndView = ctrl.onSubmit(request, response, command, exception);
    assertNotNull(modelAndView);

    assertEquals(1, application.getRepositoryCount());
    assertFalse(application.isConfigured());
    final Map model = modelAndView.getModel();
    assertEquals(new RepositoryName(repositoryName), ((Set) model.get("addedRepositories")).iterator().next());

    //assert that the config was created correctly:
    final RepositoryConfiguration configuration = application.getRepositoryConfiguration(new RepositoryName(repositoryName));
    assertEquals("http://localhost", configuration.getRepositoryUrl());
    assertTrue(configuration.isAccessControlEnabled());
    assertTrue(configuration.isZippedDownloadsAllowed());
    assertNull(configuration.getUid()); //UID only for connection testing, not stored
    assertNull(configuration.getPwd()); //PWD only for connection testing, not stored
  }

}