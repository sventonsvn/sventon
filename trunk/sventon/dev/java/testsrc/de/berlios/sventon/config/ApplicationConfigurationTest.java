package de.berlios.sventon.config;

import junit.framework.TestCase;

public class ApplicationConfigurationTest extends TestCase {

  public void testApplicationConfiguration() throws Exception {
    final ApplicationConfiguration configuration = new ApplicationConfiguration();
    assertFalse(configuration.isConfigured());
    assertEquals(0, configuration.getInstanceCount());
  }
}