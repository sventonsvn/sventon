/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Represents the sventon application version details.
 *
 * @author jesper@sventon.org
 */
public final class Version {
   /**
   * Logger for this class and subclasses.
   */
  final static Log logger = LogFactory.getLog(Version.class);

  /**
   * Version properties.
   */
  private static final Properties VERSION_PROPERTIES = new Properties();

  /**
   * Path to version.properties.
   */
  private static final String VERSION_PROPERTIES_PATH = "version.properties";

  /**
   * Release version property key.
   */
  private static final String RELEASE_VERSION_KEY_PROPERTY_KEY = "svn.release.version";

  /**
   * Private.
   */
  private Version() {
  }

  /**
   * Gets the sventon application version.
   *
   * @return The version
   */
  public static String getVersion() {
    assertMappingsLoaded();
    return VERSION_PROPERTIES.getProperty(RELEASE_VERSION_KEY_PROPERTY_KEY, "Unknown");
  }

  /**
   * Makes sure the version info are loaded, if possible. Else defaults will be used.
   */
  private static synchronized void assertMappingsLoaded() {
    if (VERSION_PROPERTIES.isEmpty()) {
      InputStream is = null;
      try {
        is = Version.class.getClassLoader().getResourceAsStream(VERSION_PROPERTIES_PATH);
        VERSION_PROPERTIES.load(is);
      } catch (Exception e) {
         logger.debug("Could not load version properties. " + (is == null ? "Property file " + VERSION_PROPERTIES_PATH + " not found. " : "") + e.getMessage(), e);
      }
    }
  }

}
