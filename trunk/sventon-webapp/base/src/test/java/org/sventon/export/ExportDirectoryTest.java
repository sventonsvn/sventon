package org.sventon.export;

import org.junit.Test;
import org.sventon.TestUtils;
import org.sventon.model.RepositoryName;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class ExportDirectoryTest {

  @Test
  public void testCreateTempFilename() throws Exception {
    final Date date = new Date(1111111111111L);
    final ExportDirectoryImpl exportDirectory = new ExportDirectoryImpl(
        new RepositoryName("defaultsvn"), new File(TestUtils.TEMP_DIR), null);

    //Since the output of SDF is dependent on local time zone we need to create the reference string
    //using SDF as well.
    final String refString = "defaultsvn-" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date) + ".zip";
    assertEquals(refString, exportDirectory.createTempFilename(date));
  }

}