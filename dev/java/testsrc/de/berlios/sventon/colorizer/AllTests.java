package de.berlios.sventon.colorizer;

import junit.framework.Test;
import junit.framework.TestSuite;
import de.berlios.sventon.svnsupport.SVNDirEntryComparatorTest;

public class AllTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for de.berlios.sventon.colorizer");
    //$JUnit-BEGIN$
    suite.addTestSuite(ColorizerTest.class);
    suite.addTestSuite(FormatterFactoryTest.class);
    suite.addTestSuite(FormatterImplTest.class);
    suite.addTestSuite(JavaFormatterTest.class);
    suite.addTestSuite(CPlusPlusFormatterTest.class);
    //$JUnit-END$
    return suite;
  }

}
