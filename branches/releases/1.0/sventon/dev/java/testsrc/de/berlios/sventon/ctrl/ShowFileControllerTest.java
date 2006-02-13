package de.berlios.sventon.ctrl;

import junit.framework.TestCase;

public class ShowFileControllerTest extends TestCase {

  public void testGetArchiveFileExtensionPattern() throws Exception {
    ShowFileController ctrl = new ShowFileController();
    ctrl.setArchiveFileExtensionPattern("(jar|zip|war|ear)");
    assertTrue("zip".matches(ctrl.archiveFileExtensionPattern));
    assertTrue("jar".matches(ctrl.archiveFileExtensionPattern));
    assertTrue("war".matches(ctrl.archiveFileExtensionPattern));
    assertTrue("ear".matches(ctrl.archiveFileExtensionPattern));
    assertFalse("EAR".matches(ctrl.archiveFileExtensionPattern));
  }
}