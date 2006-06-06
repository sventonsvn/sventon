package de.berlios.sventon.repository.cache.logmessagecache;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(AllTests.class.getPackage().getName());
    suite.addTestSuite(LogMessageCacheTest.class);
    suite.addTestSuite(LogMessageCacheUpdaterTest.class);
    return suite;
  }

}
