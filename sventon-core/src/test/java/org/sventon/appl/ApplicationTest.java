package org.sventon.appl;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockServletContext;
import org.sventon.TestUtils;
import org.sventon.model.Credentials;
import org.sventon.model.RepositoryName;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class ApplicationTest extends TestCase {

  private ConfigDirectory configDirectory;

  protected void setUp() throws Exception {
    configDirectory = TestUtils.getTestConfigDirectory();
  }

  @Override
  protected void tearDown() throws Exception {
    FileUtils.deleteDirectory(configDirectory.getConfigRootDirectory());
  }

  public void testApplicationWithoutConfigurations() throws Exception {
    try {
      new Application(null);
      fail("Should throw IAE");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setCreateDirectories(false);
    configDirectory.setServletContext(servletContext);
    final Application application = new Application(configDirectory);
    assertFalse(application.isConfigured());
    assertFalse(application.hasConfigurations());
  }

  public void testPersistRepositoryConfigurations() throws Exception {
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);

    final File repos1 = new File(configDirectory.getRepositoriesDirectory(), "testrepos1");
    final File repos2 = new File(configDirectory.getRepositoriesDirectory(), "testrepos2");

    final Application application = new Application(configDirectory);
    application.setConfigurationFileName(TestUtils.CONFIG_FILE_NAME);

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

    application.addConfiguration(repositoryConfiguration1);
    application.addConfiguration(repositoryConfiguration2);

    assertFalse(new File(repos1, TestUtils.CONFIG_FILE_NAME).exists());
    assertFalse(new File(repos2, TestUtils.CONFIG_FILE_NAME).exists());
    application.persistRepositoryConfigurations();
    //File should now be written
    assertTrue(new File(repos1, TestUtils.CONFIG_FILE_NAME).exists());
    assertTrue(new File(repos2, TestUtils.CONFIG_FILE_NAME).exists());
  }

  public void testGetConfigurationAsProperties() throws Exception {
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setCreateDirectories(false);
    configDirectory.setServletContext(servletContext);
    final Application application = new Application(configDirectory);

    final RepositoryConfiguration config1 = new RepositoryConfiguration("test1");
    config1.setRepositoryUrl("http://repo1");
    config1.setUserCredentials(new Credentials("", ""));

    final RepositoryConfiguration config2 = new RepositoryConfiguration("test2");
    config2.setRepositoryUrl("http://repo2");
    config2.setUserCredentials(new Credentials("", ""));

    assertEquals(0, application.getRepositoryConfigurationCount());
    application.addConfiguration(config1);
    assertEquals(1, application.getRepositoryConfigurationCount());
    application.addConfiguration(config2);
    assertEquals(2, application.getRepositoryConfigurationCount());
  }

  public void testGetBaseURL() throws Exception {
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setCreateDirectories(false);
    configDirectory.setServletContext(servletContext);
    final Application application = new Application(configDirectory);

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

    final String configFileName = "sventon-config-test.tmp";
    final Application application = new Application(configDirectory);
    application.setConfigurationFileName(configFileName);

    final String name = "defaultsvn";
    final File configDir = new File(configDirectory.getRepositoriesDirectory(), name);
    configDir.mkdirs();

    final File configFile = new File(configDir, configFileName);
    storeProperties(configFile, createDummyConfigProperties(name));

    assertFalse(application.hasConfigurations());
    assertFalse(application.isConfigured());
    application.loadRepositoryConfigurations(application.getConfigDirectories());
    assertEquals(1, application.getRepositoryConfigurationCount());
    assertTrue(application.isConfigured());
  }

  public void testReloadRepositoryConfigurations() throws Exception {
    final MockServletContext servletContext = new MockServletContext();
    servletContext.setContextPath("sventon-test");
    configDirectory.setServletContext(servletContext);

    final String configFileName = "sventon-config-test.tmp";
    final Application application = new Application(configDirectory);
    application.setConfigurationFileName(configFileName);

    final String name1 = "defaultsvn1";
    final String name2 = "defaultsvn2";

    final File configDir1 = new File(configDirectory.getRepositoriesDirectory(), name1);
    configDir1.mkdirs();

    final File configDir2 = new File(configDirectory.getRepositoriesDirectory(), name2);
    configDir2.mkdirs();

    storeProperties(new File(configDir1, configFileName), createDummyConfigProperties(name1));

    assertFalse(application.hasConfigurations());
    assertFalse(application.isConfigured());
    application.loadRepositoryConfigurations(application.getConfigDirectories());
    assertEquals(1, application.getRepositoryConfigurationCount());
    assertTrue(application.isConfigured());
    assertNotNull(application.getConfiguration(new RepositoryName(name1)));
    assertNull(application.getConfiguration(new RepositoryName(name2)));

    storeProperties(new File(configDir2, configFileName), createDummyConfigProperties(name2));

    application.loadRepositoryConfigurations(application.getConfigDirectories());

    assertEquals(2, application.getRepositoryConfigurationCount());
    assertNotNull(application.getConfiguration(new RepositoryName(name1)));
    assertNotNull(application.getConfiguration(new RepositoryName(name2)));
  }

  private OutputStream storeProperties(File configFile, Properties properties) throws IOException {
    OutputStream os = null;
    try {
      os = new FileOutputStream(configFile);
      properties.store(os, null);
      return os;
    } finally {
      IOUtils.closeQuietly(os);
    }
  }

  private Properties createDummyConfigProperties(String name) {
    final Properties testConfig = new Properties();
    testConfig.put("name", name);
    testConfig.put("repositoryRootUrl", "http://localhost");
    testConfig.put("userName", "userName");
    testConfig.put("userPassword", "abc123");
    testConfig.put("useCache", "false");
    testConfig.put("allowZipDownloads", "false");
    return testConfig;
  }

}