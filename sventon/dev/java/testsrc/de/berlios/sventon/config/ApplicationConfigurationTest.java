package de.berlios.sventon.config;

import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class ApplicationConfigurationTest extends TestCase {

  private static final String TEMPDIR = System.getProperty("java.io.tmpdir");

  public void testApplicationConfiguration() throws Exception {
    final ApplicationConfiguration configuration = new ApplicationConfiguration(new File(TEMPDIR), "filename");
    assertFalse(configuration.isConfigured());
    assertEquals(0, configuration.getInstanceCount());
    assertNotNull(configuration.getConfigurationDirectory());
    assertEquals("filename", configuration.getConfigurationFilename());
  }

  public void testStoreInstanceConfigurations() throws Exception {
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

    final File propFile = new File(TEMPDIR, "tmpconfigfilename");
    assertFalse(propFile.exists());

    applicationConfiguration.storeInstanceConfigurations();

    //File should now be written
    assertTrue(propFile.exists());
    propFile.delete();
    assertFalse(propFile.exists());
  }

  public void testGetConfigurationAsProperties() throws Exception {
    final ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration(new File(TEMPDIR), "filename");
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

    List<Properties> configurations = applicationConfiguration.getConfigurationAsProperties();
    Properties props = configurations.get(0);
    assertEquals(5, props.size());
    props = configurations.get(1);
    assertEquals(5, props.size());
  }

  public void testLoadInstanceConfigurations() throws Exception {

    try {
      new ApplicationConfigurator(null);
      fail("Should throw IAE");
    } catch (IllegalArgumentException iae) {
      // exptected
    }

    final Properties props = new Properties();
    props.put("defaultsvn.root", "http://localhost");
    props.put("defaultsvn.uid", "username");
    props.put("defaultsvn.pwd", "abc123");
    props.put("defaultsvn.useCache", "false");
    props.put("defaultsvn.allowZipDownloads", "false");

    final ApplicationConfiguration configuration = new ApplicationConfiguration(new File(System.getProperty("java.io.tmpdir")),
        "sventon-config-test.tmp");
    assertEquals(0, configuration.getInstanceCount());
    assertFalse(configuration.isConfigured());

    final File tempConfigFile = new File(configuration.getConfigurationDirectory(), configuration.getConfigurationFilename());
    OutputStream os = null;
    InputStream is = null;
    try {
      os = new FileOutputStream(tempConfigFile);
      props.store(os, null);

      is = new FileInputStream(tempConfigFile);
      configuration.loadInstanceConfigurations();

      assertEquals(1, configuration.getInstanceCount());
      assertTrue(configuration.isConfigured());

      final InstanceConfiguration defaultSVN = configuration.getInstanceConfiguration("defaultsvn");
      assertNotNull(defaultSVN);
      assertEquals("http://localhost", defaultSVN.getUrl());
      assertEquals("username", defaultSVN.getConfiguredUID());
      assertEquals("abc123", defaultSVN.getConfiguredPWD());
      assertFalse(defaultSVN.isCacheUsed());
      assertFalse(defaultSVN.isZippedDownloadsAllowed());
    } finally {
      IOUtils.closeQuietly(is);
      IOUtils.closeQuietly(os);
      tempConfigFile.delete();
    }

  }

}