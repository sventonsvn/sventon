package de.berlios.sventon.ctrl;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for de.berlios.sventon.ctrl");
    suite.addTestSuite(FlattenControllerTest.class);
    suite.addTestSuite(RepositoryEntryTest.class);
    suite.addTestSuite(RepositoryConfigurationTest.class);
    suite.addTestSuite(SearchControllerTest.class);
    suite.addTestSuite(GetControllerTest.class);
    return suite;
  }

}
