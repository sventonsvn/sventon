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
package de.berlios.sventon.appl;

/**
 * Represents a configured Subversion repository instance in sventon.
 *
 * @author jesper@users.berlios.de
 */
public class Instance {

  /**
   * The instance configuration.
   */
  private InstanceConfiguration configuration;

  /**
   * Gets the instance configuration.
   *
   * @return Configuration
   */
  public InstanceConfiguration getConfiguration() {
    return configuration;
  }
}
