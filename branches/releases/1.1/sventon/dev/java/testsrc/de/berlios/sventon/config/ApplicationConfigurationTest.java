package de.berlios.sventon.config;

import junit.framework.TestCase;

import java.io.File;

public class ApplicationConfigurationTest extends TestCase {

  public void testApplicationConfiguration() throws Exception {
    final ApplicationConfiguration configuration = new ApplicationConfiguration(new File("dir"), "filename");
    assertFalse(configuration.isConfigured());
    assertEquals(0, configuration.getInstanceCount());
    assertEquals("dir", configuration.getConfigurationDirectory().getName());
    assertEquals("filename", configuration.getConfigurationFilename());
  }
}