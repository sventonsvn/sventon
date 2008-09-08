package org.sventon.web.ctrl.template;

import junit.framework.TestCase;

public class ExportControllerTest extends TestCase {
  
  public void testSetArchiveFileCharset() throws Exception {
    final ExportController ctrl = new ExportController();
    assertNull(ctrl.getArchiveFileCharset());
    ctrl.setArchiveFileCharset(null);
    assertEquals(ExportController.FALLBACK_CHARSET, ctrl.getArchiveFileCharset().toString());

    ctrl.setArchiveFileCharset("UTF-8");
    assertEquals("UTF-8", ctrl.getArchiveFileCharset().toString());
  }

}
