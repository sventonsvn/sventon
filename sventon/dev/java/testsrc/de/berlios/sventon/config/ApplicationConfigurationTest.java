package de.berlios.sventon.config;

import junit.framework.TestCase;
import de.berlios.sventon.config.ApplicationConfiguration;

public class ApplicationConfigurationTest extends TestCase {

  public void testSetRepositoryRoot() throws Exception {
    ApplicationConfiguration rc = new ApplicationConfiguration();

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
  }

  public void testSetSVNConfigurationPath() throws Exception {
    ApplicationConfiguration rc = new ApplicationConfiguration();

    assertFalse(rc.isConfigured());

    rc.setSVNConfigurationPath(null);
    assertNull(rc.getSVNConfigurationPath());
    rc.setSVNConfigurationPath("");
    assertNull(rc.getSVNConfigurationPath());

    try {
      rc.setSVNConfigurationPath("/this/is/not/a/sventon/directory/and/if/it/is/-/bad/luck");
      fail("Should cause IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }
    assertNull(rc.getSVNConfigurationPath());

    rc.setSVNConfigurationPath(System.getProperty("java.io.tmpdir"));
    assertNotNull(rc.getSVNConfigurationPath());
    // Setting path is not enough...
    assertFalse(rc.isConfigured());
  }


}