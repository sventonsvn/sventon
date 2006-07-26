package de.berlios.sventon.util;

import junit.framework.TestCase;

import java.util.Properties;

public class ImageUtilTest extends TestCase {

  public void testGetThumbnailSize() throws Exception {
  }

  public void testGetContentType() throws Exception {
    ImageUtil imageUtil = getImageUtil();

    assertNull(imageUtil.getContentType("abc"));
    assertEquals("image/jpg", imageUtil.getContentType("jpg"));
    assertEquals("image/jpg", imageUtil.getContentType("jpe"));
    assertEquals("image/jpg", imageUtil.getContentType("jpeg"));
    assertEquals("image/gif", imageUtil.getContentType("gif"));
    assertEquals("image/png", imageUtil.getContentType("png"));
  }

  public void testIsImageFile() throws Exception {
    ImageUtil imageUtil = getImageUtil();

    assertTrue(imageUtil.isImageFileExtension("jpg"));
    assertFalse(imageUtil.isImageFileExtension("filenamejpg"));
    assertFalse(imageUtil.isImageFileExtension(null));
    assertFalse(imageUtil.isImageFileExtension(""));
  }

  public void testIsImageFilename() throws Exception {
    ImageUtil imageUtil = getImageUtil();

    assertTrue(imageUtil.isImageFilename("filename.gif"));
    assertFalse(imageUtil.isImageFilename("filenamejpg"));
    assertTrue(imageUtil.isImageFilename("/dir/file.gif"));
    assertFalse(imageUtil.isImageFilename(""));
    try {
      assertFalse(imageUtil.isImageFilename(null));
      fail("Should cause IllegalArgumentException");
    } catch (IllegalArgumentException ex) {
      // expected
    }
  }

  private ImageUtil getImageUtil() {
    ImageUtil imageUtil = new ImageUtil();
    Properties prop = new Properties();
    prop.setProperty("jpg", "image/jpg");
    prop.setProperty("jpe", "image/jpg");
    prop.setProperty("jpeg", "image/jpg");
    prop.setProperty("gif", "image/gif");
    prop.setProperty("png", "image/png");
    imageUtil.setMimeMappings(prop);
    return imageUtil;
  }
}