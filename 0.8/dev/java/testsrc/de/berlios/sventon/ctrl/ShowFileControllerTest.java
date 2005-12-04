package de.berlios.sventon.ctrl;

import junit.framework.TestCase;

public class ShowFileControllerTest extends TestCase {

  public void testGetArchiveFileExtensionPattern() throws Exception {
    ShowFileController ctrl = new ShowFileController();
    ctrl.setArchiveFileExtensionPattern("(jar|zip|war|ear)");
    assertTrue("zip".matches(ctrl.getArchiveFileExtensionPattern()));
    assertTrue("jar".matches(ctrl.getArchiveFileExtensionPattern()));
    assertTrue("war".matches(ctrl.getArchiveFileExtensionPattern()));
    assertTrue("ear".matches(ctrl.getArchiveFileExtensionPattern()));
    assertFalse("EAR".matches(ctrl.getArchiveFileExtensionPattern()));
  }
}