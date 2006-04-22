package de.berlios.sventon.command;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for de.berlios.sventon.command");
    suite.addTestSuite(ConfigCommandValidatorTest.class);
    suite.addTestSuite(SVNBaseCommandTest.class);
    suite.addTestSuite(SVNBaseCommandValidatorTest.class);
    return suite;
  }

}
