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

import org.apache.commons.lang.Validate;

/**
 * Represents a configured Subversion repository instance in sventon.
 *
 * @author jesper@users.berlios.de
 */
public class Instance {

  /**
   * Instance name regex pattern.
   */
  public static final String INSTANCE_NAME_PATTERN = "[a-z0-9]+";

  /**
   * Name of subversion repository intance.
   */
  private String instanceName;

  /**
   * The instance configuration.
   */
  private InstanceConfiguration configuration;

  /**
   * Flag that indicates if the cache is being updated.
   */
  private boolean updatingCache;

  /**
   * Constructor.
   *
   * @param instanceName  Instance name.
   * @param configuration Instance configuration
   * @throws IllegalArgumentException if instance name is null or does not match {@link #INSTANCE_NAME_PATTERN}.
   */
  public Instance(final String instanceName, final InstanceConfiguration configuration) {
    Validate.isTrue(isValidName(instanceName), "Name must be in lower case a-z and/or 0-9");
    this.instanceName = instanceName;
    this.configuration = configuration;
  }

  /**
   * Gets the instance configuration.
   *
   * @return Configuration
   */
  public InstanceConfiguration getConfiguration() {
    return configuration;
  }

  /**
   * Gets the name of this instance.
   *
   * @return Name
   */
  public String getName() {
    return instanceName;
  }

  /**
   * Validates given instance name.
   *
   * @param instanceName Instance name to validate.
   * @return True if valid, false if not.
   * @throws IllegalArgumentException if given name was null.
   */
  public static boolean isValidName(final String instanceName) {
    Validate.notNull(instanceName, "Instance name cannot be null");
    return instanceName.matches(INSTANCE_NAME_PATTERN);
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
