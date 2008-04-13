package de.berlios.sventon.appl;

import static de.berlios.sventon.TestUtils.TEMPDIR;
import de.berlios.sventon.TestUtils;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class ApplicationTest extends TestCase {

  public void testApplication() throws Exception {
    try {
      new Application(null, null);
      fail("Should throw IAE");
    } catch (IllegalArgumentException iae) {
      // exptected
    }

    final Application application = TestUtils.getApplicationStub();
    assertFalse(application.isConfigured());
    assertEquals(0, application.getRepositoryCount());
    assertNotNull(application.getConfigurationFile());
    assertEquals(new File(TEMPDIR, "filename"), application.getConfigurationFile());
  }

  public void testStoreRepositoryConfigurations() throws Exception {
    final File propFile = new File(TEMPDIR, "tmpconfigfilename");
    final Application application = new Application(new File(TEMPDIR), "tmpconfigfilename");

    final RepositoryConfiguration repositoryConfiguration1 = new RepositoryConfiguration("testrepos1");
    repositoryConfiguration1.setRepositoryUrl("http://localhost/1");
    repositoryConfiguration1.setUid("user1");
    repositoryConfiguration1.setPwd("abc123");
    repositoryConfiguration1.setCacheUsed(false);
    repositoryConfiguration1.setZippedDownloadsAllowed(false);

    final RepositoryConfiguration repositoryConfiguration2 = new RepositoryConfiguration("testrepos2");
    repositoryConfiguration2.setRepositoryUrl("http://localhost/2");
    repositoryConfiguration2.setUid("user2");
    repositoryConfiguration2.setPwd("123abc");
    repositoryConfiguration2.setCacheUsed(false);
    repositoryConfiguration2.setZippedDownloadsAllowed(false);

    application.addRepository(repositoryConfiguration1);
    application.addRepository(repositoryConfiguration2);

    try {
      assertFalse(propFile.exists());
      application.storeRepositoryConfigurations();

      //File should now be written
      assertTrue(propFile.exists());
    } finally {
      propFile.delete();
    }
  }

  public void testGetConfigurationAsProperties() throws Exception {
    final Application application = TestUtils.getApplicationStub();
    final RepositoryConfiguration config1 = new RepositoryConfiguration("test1");
    config1.setRepositoryUrl("http://repo1");
    config1.setUid("");
    config1.setPwd("");

    final RepositoryConfiguration config2 = new RepositoryConfiguration("test2");
    config2.setRepositoryUrl("http://repo2");
    config2.setUid("");
    config2.setPwd("");

    application.addRepository(config1);
    application.addRepository(config2);

    final List<Properties> configurations = application.getConfigurationAsProperties();
    Properties props = configurations.get(0);
    assertEquals(8, props.size());
    props = configurations.get(1);
    assertEquals(8, props.size());
  }

  public void testLoadRepositoryConfigurations() throws Exception {
    final Properties testConfig = new Properties();
    testConfig.put("defaultsvn.root", "http://localhost");
    testConfig.put("defaultsvn.uid", "username");
    testConfig.put("defaultsvn.pwd", "abc123");
    testConfig.put("defaultsvn.useCache", "false");
    testConfig.put("defaultsvn.allowZipDownloads", "false");

    final Application application = new Application(
        new File(System.getProperty("java.io.tmpdir")), "sventon-config-test.tmp");
    assertEquals(0, application.getRepositoryCount());
    assertFalse(application.isConfigured());

    final File tempConfigFile = application.getConfigurationFile();

    OutputStream os = null;
    InputStream is = null;
    try {
      os = new FileOutputStream(tempConfigFile);
      testConfig.store(os, null);

      is = new FileInputStream(tempConfigFile);
      application.loadRepositoryConfigurations();

      assertEquals(1, application.getRepositoryCount());
      assertTrue(application.isConfigured());
    } finally {
      IOUtils.closeQuietly(is);
      IOUtils.closeQuietly(os);
      tempConfigFile.delete();
    }
  }

}