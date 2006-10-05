package de.berlios.sventon.web.ctrl;

import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import de.berlios.sventon.config.ApplicationConfiguration;
import de.berlios.sventon.web.command.ConfigCommand;

import java.util.Map;
import java.util.Set;

public class ConfigurationControllerTest extends TestCase {

  public void testShowFormNonConfigured() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationController ctrl = new ConfigurationController();
    ctrl.setConfiguration(new ApplicationConfiguration("dir", "filename"));
    final ModelAndView modelAndView = ctrl.showForm(request, response, null);
    assertNotNull(modelAndView);
    assertEquals("config", modelAndView.getViewName());
  }

  public void testShowFormConfigured() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationController ctrl = new ConfigurationController();
    ApplicationConfiguration config = new ApplicationConfiguration("dir", "filename");
    config.setConfigured(true);
    ctrl.setConfiguration(config);
    final ModelAndView modelAndView = ctrl.showForm(request, response, null);
    assertNotNull(modelAndView);
    assertEquals(null, modelAndView.getViewName());
  }

  public void testProcessFormSubmissionConfigured() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationController ctrl = new ConfigurationController();
    ApplicationConfiguration config = new ApplicationConfiguration("dir", "filename");
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
    ctrl.setConfiguration(new ApplicationConfiguration("dir", "filename"));
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
    final ApplicationConfiguration configuration = new ApplicationConfiguration("dir", "filename");
    assertEquals(0, configuration.getInstanceCount());
    assertFalse(configuration.isConfigured());
    ctrl.setConfiguration(configuration);
    final ConfigCommand command = new ConfigCommand();
    command.setName("testrepos");
    command.setRepositoryURL("http://localhost");
    final BindException exception = new BindException(command, "test");
    final ModelAndView modelAndView = ctrl.processFormSubmission(request, response, command, exception);
    assertNotNull(modelAndView);
    assertEquals("config", modelAndView.getViewName());
    assertEquals(1, configuration.getInstanceCount());
    assertFalse(configuration.isConfigured());
    final Map model = modelAndView.getModel();
    assertEquals("testrepos", ((Set) model.get("addedInstances")).iterator().next());
  }

}