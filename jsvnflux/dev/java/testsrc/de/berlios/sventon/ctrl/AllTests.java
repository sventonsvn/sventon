package de.berlios.sventon.ctrl;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for de.berlios.sventon.ctrl");
    //$JUnit-BEGIN$
    suite.addTestSuite(SVNBaseCommandValidatorTest.class);
    suite.addTestSuite(ColorerTest.class);
    suite.addTestSuite(ShowLogControllerTest.class);
    suite.addTestSuite(SVNBaseCommandTest.class);
    //$JUnit-END$
    return suite;
  }

}
