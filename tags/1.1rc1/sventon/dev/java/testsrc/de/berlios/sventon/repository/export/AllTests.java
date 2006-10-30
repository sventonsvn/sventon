package de.berlios.sventon.repository.export;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(AllTests.class.getPackage().getName());
    suite.addTestSuite(ExportDirectoryTest.class);
    suite.addTestSuite(ExportFileFilterTest.class);
    suite.addTestSuite(TemporaryFileCleanerTest.class);
    return suite;
  }

}
