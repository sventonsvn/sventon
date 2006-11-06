package de.berlios.sventon.config;

import junit.framework.TestCase;

public class ApplicationConfigurationTest extends TestCase {

  public void testApplicationConfiguration() throws Exception {
    final ApplicationConfiguration configuration = new ApplicationConfiguration("dir", "filename");
    assertFalse(configuration.isConfigured());
    assertEquals(0, configuration.getInstanceCount());
    assertEquals("dir", configuration.getConfigurationDirectory());
    assertEquals("filename", configuration.getConfigurationFilename());
  }
}