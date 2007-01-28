package de.berlios.sventon.config;

import junit.framework.TestCase;

import java.io.File;

public class ApplicationConfigurationTest extends TestCase {

  private static final String TEMPDIR = System.getProperty("java.io.tmpdir");

  public void testApplicationConfiguration() throws Exception {
    final ApplicationConfiguration configuration = new ApplicationConfiguration(new File(TEMPDIR), "filename");
    assertFalse(configuration.isConfigured());
    assertEquals(0, configuration.getInstanceCount());
    assertNotNull(configuration.getConfigurationDirectory());
    assertEquals("filename", configuration.getConfigurationFilename());
  }
}