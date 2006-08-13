package de.berlios.sventon.config;

import junit.framework.TestCase;

public class ApplicationConfigurationTest extends TestCase {

  public void testSetSVNConfigurationPath() throws Exception {
    final ApplicationConfiguration ac = new ApplicationConfiguration();

    ac.setSVNConfigurationPath(null);
    assertNull(ac.getSVNConfigurationPath());
    ac.setSVNConfigurationPath("");
    assertNull(ac.getSVNConfigurationPath());

    try {
      ac.setSVNConfigurationPath("/this/is/not/a/sventon/directory/and/if/it/is/-/bad/luck");
      fail("Should cause IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }
    assertNull(ac.getSVNConfigurationPath());

    ac.setSVNConfigurationPath(System.getProperty("java.io.tmpdir"));
    assertNotNull(ac.getSVNConfigurationPath());
  }

}