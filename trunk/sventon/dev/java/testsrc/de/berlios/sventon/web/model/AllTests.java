package de.berlios.sventon.web.model;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(AllTests.class.getPackage().getName());
    suite.addTestSuite(FileExtensionListTest.class);
    suite.addTestSuite(LogEntryActionTypeTest.class);
    suite.addTestSuite(ArchiveFileTest.class);
    suite.addTestSuite(RawTextFileTest.class);
    suite.addTestSuite(TextFileTest.class);
    suite.addTestSuite(HTMLDecoratedTextFileTest.class);
    return suite;
  }

}
