package de.berlios.sventon.web.ctrl;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(AllTests.class.getPackage().getName());
    suite.addTestSuite(AbstractSVNTemplateControllerTest.class);
    suite.addTestSuite(ConfigurationControllerTest.class);
    suite.addTestSuite(ConfigurationSubmissionControllerTest.class);
    suite.addTestSuite(GetControllerTest.class);
    suite.addTestSuite(ShowFileControllerTest.class);
    suite.addTestSuite(ShowThumbnailsControllerTest.class);
    suite.addTestSuite(ZipControllerTest.class);
    return suite;
  }

}
