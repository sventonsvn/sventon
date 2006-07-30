package de.berlios.sventon.repository.export;

import junit.framework.TestCase;

public class ExportFileCleanerTest extends TestCase {

  public void testIsOld() throws Exception {
    final ExportFileCleaner cleaner = new ExportFileCleaner();
    cleaner.setTimeThreshold(500);

    final String fileName = "sventon-" + System.currentTimeMillis();
    assertFalse(cleaner.isOld(fileName));
    Thread.sleep(1000);
    assertTrue(cleaner.isOld(fileName));
  }
}