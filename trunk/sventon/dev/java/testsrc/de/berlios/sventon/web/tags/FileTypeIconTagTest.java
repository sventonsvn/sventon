package de.berlios.sventon.web.tags;

import junit.framework.TestCase;

import java.util.Properties;

public class FileTypeIconTagTest extends TestCase {

  public void testCreateImageTag() throws Exception {

    final Properties mappings = new Properties();
    mappings.put("txt", "images/icon_txt.png");

    assertEquals("<img src=\"images/icon_file.png\" alt=\"\"/>", FileTypeIconTag.createImageTag(null, mappings));
    assertEquals("<img src=\"images/icon_file.png\" alt=\"\"/>", FileTypeIconTag.createImageTag("", mappings));
    assertEquals("<img src=\"images/icon_txt.png\" alt=\"txt\"/>", FileTypeIconTag.createImageTag("filename.txt", mappings));
  }
}