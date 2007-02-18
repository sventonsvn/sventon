package de.berlios.sventon.repository.cache.revisioncache;

import junit.framework.TestSuite;
import junit.framework.Test;

public class AllTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(AllTests.class.getPackage().getName());
    suite.addTestSuite(RevisionCacheImplTest.class);
    return suite;
  }

}
