package de.berlios.sventon.appl;

import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Properties;

public class ApplicationConfigurationTest extends TestCase {

  private static final String TEMPDIR = System.getProperty("java.io.tmpdir");


  public void testLoadInstanceConfigurations() throws Exception {
    final Properties props = new Properties();
    props.put("defaultsvn.root", "http://localhost");
    props.put("defaultsvn.uid", "username");
    props.put("defaultsvn.pwd", "abc123");
    props.put("defaultsvn.useCache", "false");
    props.put("defaultsvn.allowZipDownloads", "false");

    final Application application = new Application(
        new File(System.getProperty("java.io.tmpdir")), "sventon-config-test.tmp");
    assertEquals(0, application.getInstanceCount());
    assertFalse(application.isConfigured());

    final File tempConfigFile = new File(application.getConfigurationDirectory(),
        application.getConfigurationFilename());

    OutputStream os = null;
    InputStream is = null;
    try {
      os = new FileOutputStream(tempConfigFile);
      props.store(os, null);

      is = new FileInputStream(tempConfigFile);
      application.loadInstanceConfigurations();

      assertEquals(1, application.getInstanceCount());
      assertTrue(application.isConfigured());

      final InstanceConfiguration defaultSVN = application.getInstance("defaultsvn").getConfiguration();
      assertNotNull(defaultSVN);
      assertEquals("http://localhost", defaultSVN.getRepositoryUrl());
      assertEquals("username", defaultSVN.getUid());
      assertEquals("abc123", defaultSVN.getPwd());
      assertFalse(defaultSVN.isCacheUsed());
      assertFalse(defaultSVN.isZippedDownloadsAllowed());
    } finally {
      IOUtils.closeQuietly(is);
      IOUtils.closeQuietly(os);
      tempConfigFile.delete();
    }

  }

}