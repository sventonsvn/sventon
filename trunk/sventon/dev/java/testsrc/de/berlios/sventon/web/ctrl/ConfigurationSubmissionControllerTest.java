package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.appl.InstanceConfiguration;
import de.berlios.sventon.appl.Application;
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
    final Application application = new Application(new File(TEMPDIR), "filename");
    application.setConfigured(true);
    controller.setApplication(application);
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
    final Application application = new Application(new File(TEMPDIR), "filename");
    application.setConfigured(false);
    controller.setApplication(application);

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
    

    final Application application = new Application(new File(TEMPDIR), "tmpconfigfilename");

    final InstanceConfiguration instanceConfiguration1 = new InstanceConfiguration();
    instanceConfiguration1.setRepositoryRoot("http://localhost/1");
    instanceConfiguration1.setConfiguredUID("user1");
    instanceConfiguration1.setConfiguredPWD("abc123");
    instanceConfiguration1.setCacheUsed(false);
    instanceConfiguration1.setZippedDownloadsAllowed(false);

    final InstanceConfiguration instanceConfiguration2 = new InstanceConfiguration();
    instanceConfiguration2.setRepositoryRoot("http://localhost/2");
    instanceConfiguration2.setConfiguredUID("user2");
    instanceConfiguration2.setConfiguredPWD("123abc");
    instanceConfiguration2.setCacheUsed(false);
    instanceConfiguration2.setZippedDownloadsAllowed(false);

    application.addInstance("testrepos1", instanceConfiguration1);
    application.addInstance("testrepos2", instanceConfiguration2);
    application.setConfigured(false);
    controller.setApplication(application);

    final File propFile = new File(TEMPDIR, "tmpconfigfilename");
    assertFalse(propFile.exists());

    final ModelAndView modelAndView = controller.handleRequestInternal(request, response);
    assertNotNull(modelAndView);
    assertNull(modelAndView.getViewName()); // Will be null as it is a redirect view.

    //File should now be written
    assertTrue(propFile.exists());
    propFile.delete();
    assertFalse(propFile.exists());
    assertTrue(application.isConfigured());
  }
}