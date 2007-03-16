package de.berlios.sventon.web.ctrl;

import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import de.berlios.sventon.appl.ApplicationConfiguration;
import de.berlios.sventon.appl.InstanceConfiguration;
import de.berlios.sventon.web.command.ConfigCommand;

import java.util.Map;
import java.util.Set;
import java.io.File;

public class ConfigurationControllerTest extends TestCase {

  private static final String TEMPDIR = System.getProperty("java.io.tmpdir");

  public void testShowFormNonConfigured() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationController ctrl = new ConfigurationController();
    ctrl.setConfiguration(new ApplicationConfiguration(new File(TEMPDIR), "filename"));
    final ModelAndView modelAndView = ctrl.showForm(request, response, null);
    assertNotNull(modelAndView);
    assertEquals("config", modelAndView.getViewName());
  }

  public void testShowFormConfigured() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationController ctrl = new ConfigurationController();
    ApplicationConfiguration config = new ApplicationConfiguration(new File(TEMPDIR), "filename");
    config.setConfigured(true);
    ctrl.setConfiguration(config);
    final ModelAndView modelAndView = ctrl.showForm(request, response, null);
    assertNotNull(modelAndView);
    assertEquals(null, modelAndView.getViewName());
  }

  //Test what happens if an instance is partially configured and the config view is invoked
  //this could happen if one started to configure sventon and then called repobrowser.svn.
  public void testShowFormConfiguredII() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationController ctrl = new ConfigurationController();
    final InstanceConfiguration instanceConfig = new InstanceConfiguration();
    instanceConfig.setInstanceName("firstinsance");
    ApplicationConfiguration config = new ApplicationConfiguration(new File(TEMPDIR), "filename");
    config.setConfigured(false);
    config.addInstanceConfiguration(instanceConfig);
    ctrl.setConfiguration(config);
    final ModelAndView modelAndView = ctrl.showForm(request, response, null);
    assertNotNull(modelAndView);
    assertEquals("confirmAddConfig", modelAndView.getViewName());
  }

  public void testProcessFormSubmissionConfigured() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationController ctrl = new ConfigurationController();
    ApplicationConfiguration config = new ApplicationConfiguration(new File(TEMPDIR), "filename");
    config.setConfigured(true);
    ctrl.setConfiguration(config);
    final ModelAndView modelAndView = ctrl.processFormSubmission(request, response, null, null);
    assertNotNull(modelAndView);
    assertEquals(null, modelAndView.getViewName());
  }

  public void testProcessFormSubmissionNonConfiguredValidationError() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationController ctrl = new ConfigurationController();
    ctrl.setConfiguration(new ApplicationConfiguration(new File(TEMPDIR), "filename"));
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
    final ApplicationConfiguration configuration = new ApplicationConfiguration(new File(TEMPDIR), "filename");
    assertEquals(0, configuration.getInstanceCount());
    assertFalse(configuration.isConfigured());
    ctrl.setConfiguration(configuration);
    final ConfigCommand command = new ConfigCommand();
    command.setName("testrepos");
    command.setRepositoryURL("http://localhost");
    final BindException exception = new BindException(command, "test");
    final ModelAndView modelAndView = ctrl.processFormSubmission(request, response, command, exception);
    assertNotNull(modelAndView);
    assertEquals("confirmAddConfig", modelAndView.getViewName());
    assertEquals(1, configuration.getInstanceCount());
    assertFalse(configuration.isConfigured());
    final Map model = modelAndView.getModel();
    assertEquals("testrepos", ((Set) model.get("addedInstances")).iterator().next());
  }

}