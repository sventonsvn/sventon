package de.berlios.sventon.web.ctrl;

import junit.framework.TestCase;

import java.io.File;
import java.util.Date;

public class ZipControllerTest extends TestCase {

  public void testCreateZipFile() throws Exception {
    final File tmpFile = File.createTempFile("sventon-test", ".tmp");
    try {
    final ZipController ctrl = new ZipController();
    final Date now = new Date(1111111111111L);

    final File newFile = ctrl.createZipFile("defaultsvn", tmpFile, now);
    assertEquals("defaultsvn-20050318025831111.zip", newFile.getName());
    } finally {
      tmpFile.delete();
    }
  }
}