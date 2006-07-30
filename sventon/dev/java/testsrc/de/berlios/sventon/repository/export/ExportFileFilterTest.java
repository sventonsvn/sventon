package de.berlios.sventon.repository.export;

import junit.framework.TestCase;

public class ExportFileFilterTest extends TestCase {

  public void testAccept() throws Exception {
    final ExportFileFilter filter = new ExportFileFilter();
    assertTrue(filter.accept(null, "sventon-12345.zip"));
    assertTrue(filter.accept(null, "sventon-0.zip"));
    assertFalse(filter.accept(null, "sventon-0zip"));
    assertFalse(filter.accept(null, "sventon-0.zi"));
    assertFalse(filter.accept(null, "sventon-.zip"));
    assertFalse(filter.accept(null, "sventon.zip"));
  }
}