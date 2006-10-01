package de.berlios.sventon.config;

import junit.framework.TestCase;

public class InstanceConfigurationTest extends TestCase {

  public void testSetRepositoryRoot() throws Exception {
    InstanceConfiguration rc = new InstanceConfiguration();
/*
    assertFalse(rc.isConfigured());
    rc.setRepositoryRoot(null);
    rc.setRepositoryRoot("");
    assertFalse(rc.isConfigured());
    rc.setRepositoryRoot("notanurl");
    assertFalse(rc.isConfigured());
    rc.setRepositoryRoot("notanurleither/");
    assertFalse(rc.isConfigured());
    rc.setRepositoryRoot("http://localhost");
    assertTrue(rc.isConfigured());
    rc.setRepositoryRoot("http://localhost/");
    assertTrue(rc.isConfigured());
    assertEquals("http://localhost", rc.getUrl());  // Trailing slash has been removed.
*/
  }

}
