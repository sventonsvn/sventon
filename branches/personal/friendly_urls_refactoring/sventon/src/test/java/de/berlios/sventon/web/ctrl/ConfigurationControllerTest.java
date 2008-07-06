package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.TestUtils;
import de.berlios.sventon.appl.Application;
import de.berlios.sventon.appl.RepositoryConfiguration;
import de.berlios.sventon.appl.RepositoryName;
import de.berlios.sventon.web.command.ConfigCommand;
import static de.berlios.sventon.web.command.ConfigCommand.AccessMethod.USER;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.Set;

public class ConfigurationControllerTest extends TestCase {

  public void testShowFormNonConfigured() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationController ctrl = new ConfigurationController();
    ctrl.setApplication(TestUtils.getApplicationStub());
    final ModelAndView modelAndView = ctrl.showForm(request, response, null);
    assertNotNull(modelAndView);
    assertEquals("config", modelAndView.getViewName());
  }

  public void testShowFormConfigured() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationController ctrl = new ConfigurationController();
    final Application application = TestUtils.getApplicationStub();
    application.setConfigured(true);
    ctrl.setApplication(application);
    final ModelAndView modelAndView = ctrl.showForm(request, response, null);
    assertNotNull(modelAndView);
    assertEquals(null, modelAndView.getViewName());
  }

  //Test what happens if an instance is partially configured and the config view is invoked
  //this could happen if one started to configure sventon and then called 'browse'.
  public void testShowFormConfiguredII() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationController ctrl = new ConfigurationController();

    final RepositoryConfiguration repositoryConfig = new RepositoryConfiguration("firstinstance");
    final Application application = TestUtils.getApplicationStub();
    application.setConfigured(false);
    application.addRepository(repositoryConfig);
    ctrl.setApplication(application);
    final ModelAndView modelAndView = ctrl.showForm(request, response, null);
    assertNotNull(modelAndView);
    assertEquals("confirmAddConfig", modelAndView.getViewName());
  }

  public void testProcessFormSubmissionConfigured() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationController ctrl = new ConfigurationController();
    final Application application = TestUtils.getApplicationStub();
    application.setConfigured(true);
    ctrl.setApplication(application);
    final ModelAndView modelAndView = ctrl.processFormSubmission(request, response, null, null);
    assertNotNull(modelAndView);
    assertTrue(modelAndView.getView() instanceof RedirectView);
    RedirectView rv = (RedirectView) modelAndView.getView();
    assertEquals("/repos/list", rv.getUrl());
  }

  public void testProcessFormSubmissionNonConfiguredValidationError() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationController ctrl = new ConfigurationController();
    ctrl.setApplication(TestUtils.getApplicationStub());
    final ConfigCommand command = new ConfigCommand();
    final BindException exception = new BindException(command, "test");
    exception.addError(new ObjectError("test", new String[]{}, new Object[]{}, "test message"));
    final ModelAndView modelAndView = ctrl.processFormSubmission(request, response, command, exception);
    assertNotNull(modelAndView);
    assertEquals("config", modelAndView.getViewName());
  }

  public void testProcessFormSubmissionNonConfigured() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationController ctrl = new ConfigurationController();
    final Application application = TestUtils.getApplicationStub();
    assertEquals(0, application.getRepositoryCount());
    assertFalse(application.isConfigured());
    ctrl.setApplication(application);
    final ConfigCommand command = new ConfigCommand();
    final String repositoryName = "testrepos";
    command.setName(repositoryName);
    command.setRepositoryUrl("http://localhost");
    final BindException exception = new BindException(command, "test");
    final ModelAndView modelAndView = ctrl.processFormSubmission(request, response, command, exception);
    assertNotNull(modelAndView);
    assertEquals("confirmAddConfig", modelAndView.getViewName());
    assertEquals(1, application.getRepositoryCount());
    assertFalse(application.isConfigured());
    final Map model = modelAndView.getModel();
    assertEquals(new RepositoryName(repositoryName), ((Set) model.get("addedRepositories")).iterator().next());
  }

  public void testProcessFormSubmissionNonConfiguredUserBasedAccessControl() throws Exception {
    final String repositoryName = "testrepos";

    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationController ctrl = new ConfigurationController();
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
    final ModelAndView modelAndView = ctrl.processFormSubmission(request, response, command, exception);
    assertNotNull(modelAndView);
    assertEquals("confirmAddConfig", modelAndView.getViewName());
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