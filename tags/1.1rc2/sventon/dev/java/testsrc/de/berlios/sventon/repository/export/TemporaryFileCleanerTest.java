package de.berlios.sventon.repository.export;

import junit.framework.TestCase;

import java.io.File;
import java.util.Date;

public class TemporaryFileCleanerTest extends TestCase {

  public void testIsOld() throws Exception {
    final TemporaryFileCleaner cleaner = new TemporaryFileCleaner();
    cleaner.setTimeThreshold(500);

    final File tempFile = new File("sventon-" + ExportDirectory.DATE_FORMAT.format(new Date()));
    assertFalse(cleaner.isOld(tempFile));
    Thread.sleep(1000);
    assertTrue(cleaner.isOld(tempFile));
  }
}
