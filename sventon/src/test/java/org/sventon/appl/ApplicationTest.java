package org.sventon.appl;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.sventon.TestUtils;
import static org.sventon.TestUtils.TEMPDIR;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
  }

  public void testStoreRepositoryConfigurations() throws Exception {
    final File repos1 = new File(TEMPDIR, "testrepos1");
    final File repos2 = new File(TEMPDIR, "testrepos2");

    try {
      final String configFilename = "tmpconfigfilename";
      final Application application = new Application(new File(TEMPDIR), configFilename);

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

      assertFalse(new File(repos1, configFilename).exists());
      assertFalse(new File(repos2, configFilename).exists());
      application.persistRepositoryConfigurations();
      //File should now be written
      assertTrue(new File(repos1, configFilename).exists());
      assertTrue(new File(repos2, configFilename).exists());
    } finally {
      FileUtils.deleteDirectory(repos1);
      FileUtils.deleteDirectory(repos2);
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

    assertEquals(0, application.getRepositoryCount());
    application.addRepository(config1);
    assertEquals(1, application.getRepositoryCount());
    application.addRepository(config2);
    assertEquals(2, application.getRepositoryCount());
  }

  public void testLoadRepositoryConfigurations() throws Exception {
    final Properties testConfig = new Properties();
    testConfig.put("root", "http://localhost");
    testConfig.put("uid", "username");
    testConfig.put("pwd", "abc123");
    testConfig.put("useCache", "false");
    testConfig.put("allowZipDownloads", "false");

    final File configRootDirectory = new File(TEMPDIR);
    final String configFilename = "sventon-config-test.tmp";
    final Application application = new Application(configRootDirectory, configFilename);

    assertEquals(0, application.getRepositoryCount());
    assertFalse(application.isConfigured());

    OutputStream os = null;
    InputStream is = null;

    final File configDir = new File(configRootDirectory, "defaultsvn");
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
      FileUtils.deleteDirectory(configDir);
    }
  }

}