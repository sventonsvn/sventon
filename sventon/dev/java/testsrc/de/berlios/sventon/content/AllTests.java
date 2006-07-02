package de.berlios.sventon.content;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for de.berlios.sventon.content");
    suite.addTestSuite(KeywordHandlerTest.class);
    suite.addTestSuite(LineNumberAppenderTest.class);
    return suite;
  }

}
