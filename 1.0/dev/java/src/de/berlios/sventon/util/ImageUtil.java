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
import java.util.Properties;

/**
 * Image related utility methods.
 *
 * @author jesper@users.berlios.de
 */
public final class ImageUtil {

  /**
   * Specifies the maximum horizontal/vertical size (in pixels) for a generated thumbnail image.
   */
  private int maxThumbnailSize = 200;

  private Properties mimeMappings;

  /**
   * Private - not supposed to instantiate.
   */
  public ImageUtil() {
  }

  /**
   * Converts an <code>Image</code> instance into a <code>BufferedImage</code> instance.
   *
   * @param image The <code>Image<code> instance.
   * @param type
   * @return The buffered image
   */
  public BufferedImage toBufferedImage(final Image image, final int type) {
    int width = image.getWidth(null);
    int height = image.getHeight(null);
    BufferedImage result = new BufferedImage(width, height, type);
    Graphics2D g = result.createGraphics();
    g.drawImage(image, 0, 0, null);
    g.dispose();
    return result;
  }

  /**
   * Gets the preferred thumbnail dimension for given <code>width</code>
   * and <code>height</code>.
   *
   * @param width  The width.
   * @param height The height.
   * @return The thumbnail dimension.
   */
  public Dimension getThumbnailSize(final int width, final int height) {
    int max = (width >= height) ? width : height;
    if (max <= getMaxThumbnailSize()) {
      // Image is smaller than maximum size - no need for a resize
      return new Dimension(width, height);
    } else {
      double scaleFactor = (double) max / getMaxThumbnailSize();
      return new Dimension(((int) (width / scaleFactor)), ((int) (height / scaleFactor)));
    }
  }

  /**
   * Gets the content type for given file extension.
   *
   * @param fileExtension The file extension
   * @return The content type. Null if unrecognized content type.
   */
  public String getContentType(final String fileExtension) {
    if (fileExtension == null) {
      return null;
    }
    return mimeMappings.getProperty(fileExtension.toLowerCase());
  }

  /**
   * Checks if the file extension indicates a known, browser displayable, image file.
   *
   * @param fileExtension The file extension.
   * @return True if image file, false if not.
   */
  public boolean isImageFileExtension(final String fileExtension) {
    return getContentType(fileExtension) != null;
  }

  /**
   * Checks if the extension of the file name indicates a known, browser
   * displayable, image file.
   *
   * @param filename The file name.
   * @return True if image file, false if not.
   */
  public boolean isImageFilename(final String filename) {
    String fileExtension = "";

    if (filename == null) {
      throw new IllegalArgumentException("null is not a valid filename");
    }

    if (filename.lastIndexOf(".") > -1) {
      fileExtension = filename.substring(filename.lastIndexOf(".") + 1);
    }
    return getContentType(fileExtension) != null;
  }

  /**
   * Gets the maximum thumbnail size in pixels.
   *
   * @return The size
   */
  public int getMaxThumbnailSize() {
    return maxThumbnailSize;
  }

  /**
   * Sets the maximum thumbnail size in pixels.
   *
   * @param maxThumbnailSize The size
   */
  public void setMaxThumbnailSize(int maxThumbnailSize) {
    this.maxThumbnailSize = maxThumbnailSize;
  }

  /**
   * Gets the mime-type / file type mappings.
   *
   * @return The mappings
   */
  public Properties getMimeMappings() {
    return mimeMappings;
  }

  /**
   * Sets the mime-type / file type mappings.
   *
   * @param mimeMappings The mappings
   */
  public void setMimeMappings(Properties mimeMappings) {
    this.mimeMappings = mimeMappings;
  }

}
