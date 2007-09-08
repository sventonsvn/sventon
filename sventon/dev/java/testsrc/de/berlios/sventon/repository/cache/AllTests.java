package de.berlios.sventon.repository.cache;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(AllTests.class.getPackage().getName());
    suite.addTestSuite(CacheBeforeAdviceTest.class);
    suite.addTestSuite(CacheGatewayImplTest.class);
    suite.addTestSuite(CamelCasePatternTest.class);
    return suite;
  }

}
