package de.berlios.sventon.svnsupport;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for de.berlios.sventon.svnsupport");
    //$JUnit-BEGIN$
    suite.addTestSuite(SVNDirEntryComparatorTest.class);
    suite.addTestSuite(IndexEntryTest.class);
    suite.addTestSuite(RevisionIndexTest.class);
    //$JUnit-END$
    return suite;
  }

}
