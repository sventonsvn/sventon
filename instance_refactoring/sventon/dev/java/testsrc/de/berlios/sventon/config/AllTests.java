package de.berlios.sventon.config;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(de.berlios.sventon.config.AllTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(de.berlios.sventon.config.AllTests.class.getPackage().getName());
    return suite;
  }

}
