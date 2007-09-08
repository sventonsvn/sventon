package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.config.ApplicationConfiguration;
import de.berlios.sventon.config.InstanceConfiguration;
import junit.framework.TestCase;
import org.quartz.impl.StdScheduler;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;

public class ConfigurationSubmissionControllerTest extends TestCase {

  private static final String TEMPDIR = System.getProperty("java.io.tmpdir");

  public void testHandleRequestInternalConfigured() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationSubmissionController controller = new ConfigurationSubmissionController();
    final ApplicationConfiguration config = new ApplicationConfiguration(new File(TEMPDIR), "filename");
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
    final ApplicationConfiguration config = new ApplicationConfiguration(new File(TEMPDIR), "filename");
    config.setConfigured(false);
    controller.setConfiguration(config);

    final ModelAndView modelAndView = controller.handleRequestInternal(request, response);
    assertEquals("configurationError", modelAndView.getViewName());
  }

  public void testHandleRequestInternal() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final ConfigurationSubmissionController controller = new ConfigurationSubmissionController();

    controller.setScheduler(new StdScheduler(null, null) {
      public void triggerJob(final String string, final String string1) {
      }
    });
    

    final ApplicationConfiguration applicationConfiguration =
        new ApplicationConfiguration(new File(TEMPDIR), "tmpconfigfilename");
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
    final File propFile = new File(TEMPDIR, "tmpconfigfilename");
    assertFalse(propFile.exists());

    final ModelAndView modelAndView = controller.handleRequestInternal(request, response);
    assertNotNull(modelAndView);
    assertNull(modelAndView.getViewName()); // Will be null as it is a redirect view.

    //File should now be written
    assertTrue(propFile.exists());
    propFile.delete();
    assertFalse(propFile.exists());
    assertTrue(applicationConfiguration.isConfigured());
  }
}