/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
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
    final DefaultExportDirectory exportDirectory = new DefaultExportDirectory(
        new RepositoryName("defaultsvn"), new File(TestUtils.TEMP_DIR), null);

    //Since the output of SDF is dependent on local time zone we need to create the reference string
    //using SDF as well.
    final SimpleDateFormat dateFormat = new SimpleDateFormat(DefaultExportDirectory.DATE_FORMAT_PATTERN);
    final String refString = "defaultsvn-" + dateFormat.format(date) + ".zip";
    assertEquals(refString, exportDirectory.createTempFilename(date));
  }

}