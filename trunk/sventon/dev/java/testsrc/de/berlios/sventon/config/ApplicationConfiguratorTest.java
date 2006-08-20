package de.berlios.sventon.config;

import de.berlios.sventon.util.FileUtils;
import junit.framework.TestCase;

import java.io.*;
import java.util.Properties;

public class ApplicationConfiguratorTest extends TestCase {

  public void testApplicationConfigurator() throws Exception {

    try {
      new ApplicationConfigurator((String) null, null);
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

    final ApplicationConfiguration configuration = new ApplicationConfiguration();
    assertEquals(0, configuration.getInstanceCount());
    assertFalse(configuration.isConfigured());

    File tempConfigFile = null;
    OutputStream os = null;
    InputStream is = null;
    try {
      tempConfigFile = File.createTempFile("sventon-test", ".tmp");

      os = new FileOutputStream(tempConfigFile);
      props.store(os, null);

      is = new FileInputStream(tempConfigFile);
      new ApplicationConfigurator(is, configuration);

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
      FileUtils.close(is);
      FileUtils.close(os);
      if (tempConfigFile != null) {
        tempConfigFile.delete();
      }
    }

  }

}