/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.appl;

import org.apache.commons.lang.Validate;
import org.springframework.util.StringUtils;

/**
 * Represents a configured Subversion repository instance in sventon.
 *
 * @author jesper@users.berlios.de
 */
public final class Instance {

  /**
   * The instance configuration.
   */
  private final RepositoryConfiguration configuration;

  /**
   * Flag that indicates if the cache is being updated.
   */
  private boolean updatingCache;

  /**
   * Constructor.
   *
   * @param configuration Instance configuration
   */
  public Instance(final RepositoryConfiguration configuration) {
    Validate.notNull(configuration, "Configuration cannot be null");
    this.configuration = configuration;
  }

  /**
   * Gets the instance configuration.
   *
   * @return Configuration
   */
  public RepositoryConfiguration getConfiguration() {
    return configuration;
  }

  /**
   * Validates given instance name.
   *
   * @param instanceName Instance name to validate.
   * @return True if valid, false if not.
   */
  public static boolean isValidName(final String instanceName) {
    return !(instanceName == null || instanceName.length() == 0) && !StringUtils.containsWhitespace(instanceName);
  }

  /**
   * Performs a shutdown on this instance.
   */
  public void shutdown() {
  }

  /**
   * Checks if this instance's cache is being updated.
   *
   * @return <tt>true</tt> if cache is being updated.
   */
  public boolean isUpdatingCache() {
    return updatingCache;
  }

  /**
   * Sets the cache updating status.
   * <p/>
   * Note: This method is package protected by design.
   *
   * @param updatingCache True or false.
   */
  void setUpdatingCache(final boolean updatingCache) {
    this.updatingCache = updatingCache;
  }

}
