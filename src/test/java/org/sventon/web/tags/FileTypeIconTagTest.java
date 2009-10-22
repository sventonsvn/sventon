package org.sventon.web.tags;

import junit.framework.TestCase;

import java.util.Properties;

public class FileTypeIconTagTest extends TestCase {

  public void testCreateImageTag() throws Exception {
    final Properties mappings = new Properties();
    mappings.put("txt", "images/icon_txt.png");
    mappings.put("war", "images/icon_file_zip.png;Web archive (WAR)");

    final FileTypeIconTag iconTag = new FileTypeIconTag();

    try {
      iconTag.createImageTag(null, mappings);
      fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // expected
    }

    assertEquals("<img src=\"images/icon_file.png\" title=\"\" alt=\"\">", iconTag.createImageTag("", mappings));
    assertEquals("<img src=\"images/icon_txt.png\" title=\"txt\" alt=\"txt\">", iconTag.createImageTag("filename.txt", mappings));
    assertEquals("<img src=\"images/icon_txt.png\" title=\"txt\" alt=\"txt\">", iconTag.createImageTag("filename.TXT", mappings));

    assertEquals("<img src=\"images/icon_file_zip.png\" title=\"Web archive (WAR)\" alt=\"Web archive (WAR)\">", iconTag.createImageTag("filename.war", mappings));
  }

  public void testExtractDescriptionFromMapping() throws Exception {
    final FileTypeIconTag iconTag = new FileTypeIconTag();

    assertEquals(null, iconTag.extractDescriptionFromMapping(null));
    assertEquals(null, iconTag.extractDescriptionFromMapping(""));
    assertEquals(null, iconTag.extractDescriptionFromMapping("images/icon_file_zip.png"));
    assertEquals(null, iconTag.extractDescriptionFromMapping("images/icon_file_zip.png "));
    assertEquals(null, iconTag.extractDescriptionFromMapping("images/icon_file_zip.png;"));
    assertEquals("Web archive", iconTag.extractDescriptionFromMapping("images/icon_file_zip.png;Web archive"));
  }

  public void testExtractIconFromMapping() throws Exception {
    final FileTypeIconTag iconTag = new FileTypeIconTag();

    assertEquals("images/icon_file.png", iconTag.extractIconFromMapping(null));
    assertEquals("images/icon_file.png", iconTag.extractIconFromMapping(""));
    assertEquals("images/icon_file_zip.png", iconTag.extractIconFromMapping("images/icon_file_zip.png"));
    assertEquals("images/icon_file_zip.png", iconTag.extractIconFromMapping("images/icon_file_zip.png "));
    assertEquals("images/icon_file_zip.png", iconTag.extractIconFromMapping("images/icon_file_zip.png;"));
    assertEquals("images/icon_file_zip.png", iconTag.extractIconFromMapping("images/icon_file_zip.png;Web archive"));
  }
}