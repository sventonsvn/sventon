package de.berlios.sventon.util;

import junit.framework.TestCase;

import java.awt.*;

public class ImageUtilTest extends TestCase {

  public void testGetThumbnailSize() throws Exception {
    assertEquals(new Dimension(100, 100), ImageUtil.getThumbnailSize(100, 100));
    assertEquals(new Dimension(200, 150), ImageUtil.getThumbnailSize(1024, 768));
    assertEquals(new Dimension(200, 150), ImageUtil.getThumbnailSize(1600, 1200));
    assertEquals(new Dimension(200, 100), ImageUtil.getThumbnailSize(1600, 800));
    assertEquals(new Dimension(200, 19), ImageUtil.getThumbnailSize(505, 48));
    assertEquals(new Dimension(179, 39), ImageUtil.getThumbnailSize(179, 39));
    assertEquals(new Dimension(200, 56), ImageUtil.getThumbnailSize(214, 60));
    assertEquals(new Dimension(200, 99), ImageUtil.getThumbnailSize(399, 199));
    assertEquals(new Dimension(99, 200), ImageUtil.getThumbnailSize(199, 399));
    assertEquals(new Dimension(66, 200), ImageUtil.getThumbnailSize(600, 1800));
    assertEquals(new Dimension(4, 200), ImageUtil.getThumbnailSize(60, 2800));
    assertEquals(new Dimension(0, 200), ImageUtil.getThumbnailSize(10, 4000));
  }

  public void testGetContentType() throws Exception {
    assertNull(ImageUtil.getContentType("abc"));
    assertEquals("image/jpg", ImageUtil.getContentType("jpg"));
  }
}