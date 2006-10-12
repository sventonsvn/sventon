package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.config.ApplicationConfiguration;
import de.berlios.sventon.config.InstanceConfiguration;
import junit.framework.TestCase;
import org.quartz.impl.StdScheduler;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.Properties;

public class ConfigurationSubmissionControllerTest extends TestCase {

  public void testHandleRequestInternalConfigured() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationSubmissionController controller = new ConfigurationSubmissionController();
    final ApplicationConfiguration config = new ApplicationConfiguration("dir", "filename");
    config.setConfigured(true);
    controller.setConfiguration(config);
    try {
      controller.handleRequestInternal(request, response);
      fail("Should throw IllegalStateException");
    } catch (IllegalStateException ise) {
      // expected
    }
  }

  public void testHandleRequestInternalNoAddedInstance() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationSubmissionController controller = new ConfigurationSubmissionController();
    final ApplicationConfiguration config = new ApplicationConfiguration("dir", "filename");
    config.setConfigured(false);
    controller.setConfiguration(config);

    try {
      controller.handleRequestInternal(request, response);
      fail("Should throw IllegalStateException");
    } catch (IllegalStateException ise) {
      // expected
    }
  }

  public void testHandleRequestInternal() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationSubmissionController controller = new ConfigurationSubmissionController();
    final StaticWebApplicationContext applicationContext = new StaticWebApplicationContext();
    final MockServletContext servletContext = new MockServletContext() {
      public String getRealPath(final String string) {
        return System.getProperty("java.io.tmpdir");
      }
    };
    applicationContext.setServletContext(servletContext);
    controller.setApplicationContext(applicationContext);
    controller.setScheduler(new StdScheduler(null, null) {
      public void triggerJob(final String string, final String string1) {
      }
    });

    final ApplicationConfiguration applicationConfiguration =
        new ApplicationConfiguration(System.getProperty("java.io.tmpdir"), "tmpconfigfilename");
    final InstanceConfiguration instanceConfiguration1 = new InstanceConfiguration();
    instanceConfiguration1.setInstanceName("testrepos1");
    instanceConfiguration1.setRepositoryRoot("http://localhost/1");
    instanceConfiguration1.setConfiguredUID("user1");
    instanceConfiguration1.setConfiguredPWD("abc123");
    instanceConfiguration1.setCacheUsed(false);
    instanceConfiguration1.setZippedDownloadsAllowed(false);

    final InstanceConfiguration instanceConfiguration2 = new InstanceConfiguration();
    instanceConfiguration2.setInstanceName("testrepos2");
    instanceConfiguration2.setRepositoryRoot("http://localhost/2");
    instanceConfiguration2.setConfiguredUID("user2");
    instanceConfiguration2.setConfiguredPWD("123abc");
    instanceConfiguration2.setCacheUsed(false);
    instanceConfiguration2.setZippedDownloadsAllowed(false);

    applicationConfiguration.addInstanceConfiguration(instanceConfiguration1);
    applicationConfiguration.addInstanceConfiguration(instanceConfiguration2);
    applicationConfiguration.setConfigured(false);
    controller.setConfiguration(applicationConfiguration);
    final ModelAndView modelAndView = controller.handleRequestInternal(request, response);
    assertNotNull(modelAndView);
    assertNull(modelAndView.getViewName()); // Will be null as it is a redirect view.
    final File propFile = new File(System.getProperty("java.io.tmpdir"), "tmpconfigfilename");
    assertTrue(propFile.exists());
    propFile.delete();
    assertFalse(propFile.exists());
    assertTrue(applicationConfiguration.isConfigured());
  }

  public void testGetConfigurationAsProperties() throws Exception {
    final ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration("dir", "filename");
    final InstanceConfiguration config1 = new InstanceConfiguration();
    config1.setInstanceName("test1");
    config1.setRepositoryRoot("http://repo1");
    config1.setConfiguredUID("");
    config1.setConfiguredPWD("");

    final InstanceConfiguration config2 = new InstanceConfiguration();
    config2.setInstanceName("test2");
    config2.setRepositoryRoot("http://repo2");
    config2.setConfiguredUID("");
    config2.setConfiguredPWD("");

    applicationConfiguration.addInstanceConfiguration(config1);
    applicationConfiguration.addInstanceConfiguration(config2);

    Properties props = new ConfigurationSubmissionController().getConfigurationAsProperties(applicationConfiguration).get(0);
    assertEquals(5, props.size());
    props = new ConfigurationSubmissionController().getConfigurationAsProperties(applicationConfiguration).get(1);
    assertEquals(5, props.size());
  }
}