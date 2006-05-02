package de.berlios.sventon.command;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(AllTests.class.getPackage().getName());
    suite.addTestSuite(ConfigCommandValidatorTest.class);
    suite.addTestSuite(DiffCommandTest.class);
    suite.addTestSuite(SVNBaseCommandTest.class);
    suite.addTestSuite(SVNBaseCommandValidatorTest.class);
    return suite;
  }

}
