package de.berlios.sventon.util;

import java.awt.image.BufferedImage;
import java.awt.*;

/**
 * Image related utility methods.
 *
 * @author jesper@users.berlios.de
 */
public final class ImageUtil {

  /**
   * Preferred thumbnail max size (width and height).
   */
  public static final int THUMBNAIL_SIZE = 200;

  /**
   * Private - not supposed to instantiate.
   */
  private ImageUtil() {
  }

  /**
   * Converts an <code>Image</code> instance into a <code>BufferedImage</code> instance.
   *
   * @param image The <code>Image<code> instance.
   * @param type
   * @return The buffered image
   */
  public static BufferedImage toBufferedImage(final Image image, final int type) {
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
  public static Dimension getThumbnailSize(final int width, final int height) {
    int max = (width >= height) ? width : height;
    if (max <= THUMBNAIL_SIZE) {
      // Image is smaller than 200px - no need for a resize.
      return new Dimension(width, height);
    } else {
      double scaleFactor = (double) max / THUMBNAIL_SIZE;
      return new Dimension(((int) (width / scaleFactor)), ((int) (height / scaleFactor)));
    }
  }

  /**
   * Gets the content type for given file extension.
   *
   * @param fileExtension The file extension
   * @return The content type. Null if unrecognized content type.
   */
  public static String getContentType(final String fileExtension) {
    String contentType = null;
    if ("gif".equalsIgnoreCase(fileExtension)) {
      contentType = "image/gif";
    } else if ("png".equalsIgnoreCase(fileExtension)) {
      contentType = "image/png";
    } else if ("jpg".equalsIgnoreCase(fileExtension)) {
      contentType = "image/jpg";
    } else if ("jpe".equalsIgnoreCase(fileExtension)) {
      contentType = "image/jpg";
    } else if ("jpeg".equalsIgnoreCase(fileExtension)) {
      contentType = "image/jpg";
    }
    return contentType;
  }

  /**
   * Checks if the file extension indicates a know, browser displayable, image file.
   *
   * @param fileExtension The file extension.
   * @return True if image file, false if not.
   */
  public static boolean isImageFile(String fileExtension) {
    //TODO: Better handling of file and content types.
    return ImageUtil.getContentType(fileExtension) != null;
  }

}
