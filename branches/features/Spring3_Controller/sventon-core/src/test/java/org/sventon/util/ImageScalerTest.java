package org.sventon.util;

import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertEquals;

public class ImageScalerTest {

  @Test
  public void testGetThumbnail() throws Exception {
    final ImageScaler imageScaler = new ImageScaler();

    int maxSize = 200;
    assertEquals(new Dimension(100, 100), imageScaler.getThumbnailSize(100, 100, maxSize));
    assertEquals(new Dimension(200, 150), imageScaler.getThumbnailSize(1024, 768, maxSize));
    assertEquals(new Dimension(200, 150), imageScaler.getThumbnailSize(1600, 1200, maxSize));
    assertEquals(new Dimension(200, 100), imageScaler.getThumbnailSize(1600, 800, maxSize));
    assertEquals(new Dimension(200, 19), imageScaler.getThumbnailSize(505, 48, maxSize));
    assertEquals(new Dimension(179, 39), imageScaler.getThumbnailSize(179, 39, maxSize));
    assertEquals(new Dimension(200, 56), imageScaler.getThumbnailSize(214, 60, maxSize));
    assertEquals(new Dimension(200, 99), imageScaler.getThumbnailSize(399, 199, maxSize));
    assertEquals(new Dimension(99, 200), imageScaler.getThumbnailSize(199, 399, maxSize));
    assertEquals(new Dimension(66, 200), imageScaler.getThumbnailSize(600, 1800, maxSize));
    assertEquals(new Dimension(4, 200), imageScaler.getThumbnailSize(60, 2800, maxSize));
    assertEquals(new Dimension(0, 200), imageScaler.getThumbnailSize(10, 4000, maxSize));
  }
}