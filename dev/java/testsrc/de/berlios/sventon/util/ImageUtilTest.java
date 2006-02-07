package de.berlios.sventon.util;

import junit.framework.TestCase;

import java.awt.*;
import java.util.Properties;

public class ImageUtilTest extends TestCase {

  public void testGetThumbnailSize() throws Exception {
    ImageUtil imageUtil = getImageUtil();

    assertEquals(new Dimension(100, 100), imageUtil.getThumbnailSize(100, 100));
    assertEquals(new Dimension(200, 150), imageUtil.getThumbnailSize(1024, 768));
    assertEquals(new Dimension(200, 150), imageUtil.getThumbnailSize(1600, 1200));
    assertEquals(new Dimension(200, 100), imageUtil.getThumbnailSize(1600, 800));
    assertEquals(new Dimension(200, 19), imageUtil.getThumbnailSize(505, 48));
    assertEquals(new Dimension(179, 39), imageUtil.getThumbnailSize(179, 39));
    assertEquals(new Dimension(200, 56), imageUtil.getThumbnailSize(214, 60));
    assertEquals(new Dimension(200, 99), imageUtil.getThumbnailSize(399, 199));
    assertEquals(new Dimension(99, 200), imageUtil.getThumbnailSize(199, 399));
    assertEquals(new Dimension(66, 200), imageUtil.getThumbnailSize(600, 1800));
    assertEquals(new Dimension(4, 200), imageUtil.getThumbnailSize(60, 2800));
    assertEquals(new Dimension(0, 200), imageUtil.getThumbnailSize(10, 4000));
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
    imageUtil.setMaxThumbnailSize(200);
    return imageUtil;
  }
}