package de.berlios.sventon.repository;

import junit.framework.TestCase;
import de.berlios.sventon.repository.RepositoryConfiguration;

public class RepositoryConfigurationTest extends TestCase {

  public void testSetRepositoryRoot() throws Exception {
    RepositoryConfiguration rc = new RepositoryConfiguration();

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
    RepositoryConfiguration rc = new RepositoryConfiguration();

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