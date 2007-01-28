package de.berlios.sventon.web.ctrl;

import junit.framework.TestCase;
import de.berlios.sventon.web.ctrl.ShowFileController;

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