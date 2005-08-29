package de.berlios.sventon;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for de.berlios.sventon");
    //$JUnit-BEGIN$
    suite.addTest(de.berlios.sventon.ctrl.AllTests.suite());
    suite.addTest(de.berlios.sventon.index.AllTests.suite());
    suite.addTest(de.berlios.sventon.svnsupport.AllTests.suite());
    suite.addTest(de.berlios.sventon.util.AllTests.suite());
    //$JUnit-END$
    return suite;
  }

}
