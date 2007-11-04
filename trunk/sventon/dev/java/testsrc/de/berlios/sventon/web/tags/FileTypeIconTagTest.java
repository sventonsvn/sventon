package de.berlios.sventon.web.tags;

import junit.framework.TestCase;

import java.util.Properties;

public class FileTypeIconTagTest extends TestCase {

  public void testCreateImageTag() throws Exception {
    final Properties mappings = new Properties();
    mappings.put("txt", "images/icon_txt.png");

    try {
      FileTypeIconTag.createImageTag(null, mappings);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // expected
    }
    assertEquals("<img src=\"images/icon_file.png\" title=\"\" alt=\"\"/>", FileTypeIconTag.createImageTag("", mappings));
    assertEquals("<img src=\"images/icon_txt.png\" title=\"txt\" alt=\"txt\"/>", FileTypeIconTag.createImageTag("filename.txt", mappings));
    assertEquals("<img src=\"images/icon_txt.png\" title=\"txt\" alt=\"txt\"/>", FileTypeIconTag.createImageTag("filename.TXT", mappings));
  }
}