package org.sventon.appl;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockServletContext;
import org.sventon.TestUtils;
import org.sventon.model.Credentials;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ApplicationTest extends TestCase {

  private ConfigDirectory configDirectory;

  protected void setUp() throws Exception {
    configDirectory = TestUtils.getTestConfigDirectory();
  }

  public void testApplication() throws Exception {
    try {
      new Application(null, null);
      fail("Should throw IAE");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setCreateDirectories(false);
    configDirectory.setServletContext(servletContext);
    final Application application = new Application(configDirectory, TestUtils.CONFIG_FILE_NAME);
    assertFalse(application.isConfigured());
    assertEquals(0, application.getRepositoryCount());
  }

  public void testStoreRepositoryConfigurations() throws Exception {
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);

    final File repos1 = new File(configDirectory.getRepositoriesDirectory(), "testrepos1");
    final File repos2 = new File(configDirectory.getRepositoriesDirectory(), "testrepos2");

    try {
      final Application application = new Application(configDirectory, TestUtils.CONFIG_FILE_NAME);

      final RepositoryConfiguration repositoryConfiguration1 = new RepositoryConfiguration("testrepos1");
      repositoryConfiguration1.setRepositoryUrl("http://localhost/1");
      repositoryConfiguration1.setUserCredentials(new Credentials("user1", "abc123"));
      repositoryConfiguration1.setCacheUsed(false);
      repositoryConfiguration1.setZippedDownloadsAllowed(false);

      final RepositoryConfiguration repositoryConfiguration2 = new RepositoryConfiguration("testrepos2");
      repositoryConfiguration2.setRepositoryUrl("http://localhost/2");
      repositoryConfiguration2.setUserCredentials(new Credentials("user2", "123abc"));
      repositoryConfiguration2.setCacheUsed(false);
      repositoryConfiguration2.setZippedDownloadsAllowed(false);

      application.addRepository(repositoryConfiguration1);
      application.addRepository(repositoryConfiguration2);

      assertFalse(new File(repos1, TestUtils.CONFIG_FILE_NAME).exists());
      assertFalse(new File(repos2, TestUtils.CONFIG_FILE_NAME).exists());
      application.persistRepositoryConfigurations();
      //File should now be written
      assertTrue(new File(repos1, TestUtils.CONFIG_FILE_NAME).exists());
      assertTrue(new File(repos2, TestUtils.CONFIG_FILE_NAME).exists());
    } finally {
      FileUtils.deleteDirectory(configDirectory.getConfigRootDirectory());
    }
  }

  public void testGetConfigurationAsProperties() throws Exception {
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setCreateDirectories(false);
    configDirectory.setServletContext(servletContext);
    final Application application = new Application(configDirectory, TestUtils.CONFIG_FILE_NAME);

    final RepositoryConfiguration config1 = new RepositoryConfiguration("test1");
    config1.setRepositoryUrl("http://repo1");
    config1.setUserCredentials(new Credentials("", ""));

    final RepositoryConfiguration config2 = new RepositoryConfiguration("test2");
    config2.setRepositoryUrl("http://repo2");
    config2.setUserCredentials(new Credentials("", ""));

    assertEquals(0, application.getRepositoryCount());
    application.addRepository(config1);
    assertEquals(1, application.getRepositoryCount());
    application.addRepository(config2);
    assertEquals(2, application.getRepositoryCount());
  }

  public void testGetBaseURL() throws Exception {
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setCreateDirectories(false);
    configDirectory.setServletContext(servletContext);
    final Application application = new Application(configDirectory, TestUtils.CONFIG_FILE_NAME);

    assertNull(application.getBaseURL());

    System.setProperty(Application.PROPERTY_KEY_SVENTON_BASE_URL, "not-a-url");
    assertNull(application.getBaseURL());

    System.setProperty(Application.PROPERTY_KEY_SVENTON_BASE_URL, "http://validurl");
    assertEquals("http://validurl/", application.getBaseURL().toString());

    System.setProperty(Application.PROPERTY_KEY_SVENTON_BASE_URL, "http://validurl:81/");
    assertEquals("http://validurl:81/", application.getBaseURL().toString());
  }

  public void testLoadRepositoryConfigurations() throws Exception {
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);

    final Properties testConfig = new Properties();
    testConfig.put("repositoryRootUrl", "http://localhost");
    testConfig.put("userName", "userName");
    testConfig.put("userPassword", "abc123");
    testConfig.put("useCache", "false");
    testConfig.put("allowZipDownloads", "false");

    final String configFilename = "sventon-config-test.tmp";
    final Application application = new Application(configDirectory, configFilename);

    assertEquals(0, application.getRepositoryCount());
    assertFalse(application.isConfigured());

    OutputStream os = null;
    InputStream is = null;

    final File configDir = new File(configDirectory.getRepositoriesDirectory(), "defaultsvn");
    configDir.mkdirs();

    try {
      os = new FileOutputStream(new File(configDir, configFilename));
      testConfig.store(os, null);

      application.loadRepositoryConfigurations();

      assertEquals(1, application.getRepositoryCount());
      assertTrue(application.isConfigured());
    } finally {
      IOUtils.closeQuietly(is);
      IOUtils.closeQuietly(os);
      FileUtils.deleteDirectory(configDirectory.getConfigRootDirectory());
    }
  }

}