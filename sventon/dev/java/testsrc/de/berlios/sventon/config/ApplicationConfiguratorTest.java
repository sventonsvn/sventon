package de.berlios.sventon.config;

import junit.framework.TestCase;

public class ApplicationConfiguratorTest extends TestCase {

  public void testApplicationConfigurator() throws Exception {

    try {
      new ApplicationConfigurator(null);
      fail("Should throw IAE");
    } catch (IllegalArgumentException iae) {
      // exptected
    }
  }
}