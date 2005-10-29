package de.berlios.sventon.diff;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for de.berlios.sventon.diff");
    suite.addTestSuite(DiffTest.class);
    suite.addTestSuite(DiffProducerTest.class);
    suite.addTestSuite(DiffResultParserTest.class);
    return suite;
  }

}
