package de.berlios.sventon.repository;

import junit.framework.Test;
import junit.framework.TestSuite;
import de.berlios.sventon.config.ApplicationConfigurationTest;

public class AllTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(AllTests.class.getPackage().getName());
    suite.addTestSuite(LogMessageComparatorTest.class);
    suite.addTestSuite(RevisionObservableImplTest.class);
    suite.addTestSuite(ApplicationConfigurationTest.class);
    suite.addTestSuite(RepositoryEntryTest.class);
    suite.addTestSuite(RepositoryEntryComparatorTest.class);
    return suite;
  }

}
