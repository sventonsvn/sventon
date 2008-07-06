package de.berlios.sventon.repository.export;

import de.berlios.sventon.appl.RepositoryName;
import junit.framework.TestCase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExportDirectoryTest extends TestCase {

  public void testCreateTempFilename() throws Exception {
    final Date date = new Date(1111111111111L);
    final ExportDirectory exportDirectory = new ExportDirectory(
        new RepositoryName("defaultsvn"), new File(System.getProperty("java.io.tmpdir")), null);

    //Since the output of SDF is dependent on local time zone we need to create the reference string
    //using SDF as well.
    final String refString = "defaultsvn-" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date) + ".zip";
    assertEquals(refString, exportDirectory.createTempFilename(date));
  }

}