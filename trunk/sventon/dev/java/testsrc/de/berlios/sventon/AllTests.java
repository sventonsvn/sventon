package de.berlios.sventon;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for de.berlios.sventon");
    suite.addTest(de.berlios.sventon.blame.AllTests.suite());
    suite.addTest(de.berlios.sventon.cache.AllTests.suite());
    suite.addTest(de.berlios.sventon.colorer.AllTests.suite());
    suite.addTest(de.berlios.sventon.command.AllTests.suite());
    suite.addTest(de.berlios.sventon.content.AllTests.suite());
    suite.addTest(de.berlios.sventon.ctrl.AllTests.suite());
    suite.addTest(de.berlios.sventon.ctrl.xml.AllTests.suite());
    suite.addTest(de.berlios.sventon.diff.AllTests.suite());
    suite.addTest(de.berlios.sventon.rss.AllTests.suite());
    suite.addTest(de.berlios.sventon.index.AllTests.suite());
    suite.addTest(de.berlios.sventon.svnsupport.AllTests.suite());
    suite.addTest(de.berlios.sventon.util.AllTests.suite());
    return suite;
  }

}
