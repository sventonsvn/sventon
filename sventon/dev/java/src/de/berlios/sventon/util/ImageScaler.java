/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.util;

import java.awt.image.BufferedImage;
import java.awt.*;

/**
 * Class for scaling images.
 *
 * @author jesper@users.berlios.de
 */
public class ImageScaler {

  private final BufferedImage image;
  private final int width;
  private final int height;

  /**
   * Constructor.
   *
   * @param image The image to scale.
   */
  public ImageScaler(final BufferedImage image) {
    this.image = image;
    this.width = image.getWidth();
    this.height = image.getHeight();
  }

  /**
   * Gets a thumbnail version of the buffered image.
   *
   * @param maxSize Maximum size (height and width)
   * @return The thumbnail.
   */
  public BufferedImage getThumbnail(final int maxSize) {
    // Get preferred thumbnail dimension.
    final Dimension thumbnailSize = getThumbnailSize(width, height, maxSize);

    // Resize image.
    final Image rescaled = image.getScaledInstance((int) thumbnailSize.getWidth(),
        (int) thumbnailSize.getHeight(), Image.SCALE_AREA_AVERAGING);

    return toBufferedImage(rescaled, BufferedImage.TYPE_INT_ARGB);
  }

  /**
   * Gets the preferred thumbnail dimension for given <code>width</code>
   * and <code>height</code>.
   *
   * @param width   The width.
   * @param height  The height.
   * @param maxSize Maximum size (height and width)
   * @return The thumbnail dimension.
   */
  protected Dimension getThumbnailSize(final int width, final int height, final int maxSize) {
    int max = (width >= height) ? width : height;
    if (max <= maxSize) {
      // Image is smaller than maximum size - no need for a resize
      return new Dimension(width, height);
    } else {
      double scaleFactor = (double) max / maxSize;
      return new Dimension(((int) (width / scaleFactor)), ((int) (height / scaleFactor)));
    }
  }

  /**
   * Converts an <code>Image</code> instance into a <code>BufferedImage</code> instance.
   *
   * @param image The <code>Image<code> instance.
   * @param type
   * @return The buffered image
   * @see java.awt.image.BufferedImage
   */
  private BufferedImage toBufferedImage(final Image image, final int type) {
    int width = image.getWidth(null);
    int height = image.getHeight(null);

    final BufferedImage result = new BufferedImage(width, height, type);
    final Graphics2D g = result.createGraphics();
    g.drawImage(image, 0, 0, null);
    g.dispose();
    return result;
  }

}
