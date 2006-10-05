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
package de.berlios.sventon;

/**
 * Represents the sventon application version details.
 * Keywords in the source will be replaced with correct
 * data during build process.
 *
 * @author jesper@users.berlios.de
 */
public final class Version {

  private static final String VERSION = "@VERSION@";

  private static final String REVISION = "@REVISION@";

  /**
   * Gets the sventon application version.
   *
   * @return The version
   */
  public static String getVersion() {
    return VERSION;
  }

  /**
   * Gets the sventon application source revision.
   *
   * @return The revision
   */
  public static String getRevision() {
    return REVISION;
  }
}
