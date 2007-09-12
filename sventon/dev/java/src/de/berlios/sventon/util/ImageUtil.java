/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.util;

import java.util.Properties;

/**
 * Image related utility methods.
 *
 * @author jesper@users.berlios.de
 */
public final class ImageUtil {

  /**
   * Mime type / file type mappings.
   */
  private Properties mimeMappings;

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
  public void setMimeMappings(final Properties mimeMappings) {
    this.mimeMappings = mimeMappings;
  }

}
