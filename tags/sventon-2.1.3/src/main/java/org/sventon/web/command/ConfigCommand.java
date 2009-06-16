/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.command;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.beans.BeanUtils;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.model.Credentials;

/**
 * ConfigCommand.
 * <p/>
 * Command class used to bind and pass servlet parameter arguments for sventon configuration.
 *
 * @author jesper@sventon.org
 * @author patrik@sventon.org
 */
public final class ConfigCommand {

  /**
   * Available access methods.
   */
  public enum AccessMethod {

    /**
     * Anonymous access (no uid/pwd needed).
     */
    ANONYMOUS(),

    /**
     * Shared access, one uid/pwd for the entire repos, set globally in sventon, transparent
     * for sventon user.
     */
    SHARED(),

    /**
     * User access, each sventon user needs supply it's own uid/pwd for accessing restricted
     * parts of the repository.
     */
    USER()
  }

  private String name;
  private String repositoryUrl;

  private String userName;
  private String userPassword;

  private String cacheUserName;
  private String cacheUserPassword;

  private boolean useCache;
  private boolean zipDownloadsAllowed;

  private ConfigCommand.AccessMethod accessMethod = AccessMethod.ANONYMOUS;

  /**
   * Gets the repository URL.
   *
   * @return The repository URL, (trimmed if necessary)
   */
  public String getRepositoryUrl() {
    return repositoryUrl == null ? null : repositoryUrl.trim();
  }

  /**
   * Sets the repository URL.
   *
   * @param repositoryUrl URL.
   */
  public void setRepositoryUrl(final String repositoryUrl) {
    this.repositoryUrl = repositoryUrl;
  }

  /**
   * Gets the user ID.
   *
   * @return User ID.
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Sets the user ID.
   *
   * @param userName User ID.
   */
  public void setUserName(final String userName) {
    this.userName = userName;
  }

  /**
   * Gets the user password.
   *
   * @return User password.
   */
  public String getUserPassword() {
    return userPassword;
  }

  /**
   * Sets the user password.
   *
   * @param userPassword User password.
   */
  public void setUserPassword(final String userPassword) {
    this.userPassword = userPassword;
  }

  /**
   * Checks if cache is enabled.
   *
   * @return True if cache is enabled, false if not.
   */
  public boolean isCacheUsed() {
    return useCache;
  }

  /**
   * Sets the cache enable flag.
   *
   * @param useCache True to enable cache, false if not.
   */
  public void setCacheUsed(final boolean useCache) {
    this.useCache = useCache;
  }

  /**
   * Get access control method.
   *
   * @return Access control method
   */
  public AccessMethod getAccessMethod() {
    return accessMethod;
  }

  /**
   * Sets the access control method.
   *
   * @param accessMethod Access control method
   */
  public void setAccessMethod(final AccessMethod accessMethod) {
    this.accessMethod = accessMethod;
  }

  /**
   * Checks if zipped downloads are enabled.
   *
   * @return True if enabled, false if not.
   */
  public boolean isZippedDownloadsAllowed() {
    return zipDownloadsAllowed;
  }

  /**
   * Sets the zipped downloads enable flag.
   *
   * @param zipDownloadsAllowed True to enable zipped downloads, false if not.
   */
  public void setZippedDownloadsAllowed(final boolean zipDownloadsAllowed) {
    this.zipDownloadsAllowed = zipDownloadsAllowed;
  }

  /**
   * Gets the repository name.
   *
   * @return Name of repository.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the repository name.
   *
   * @param name Name of repository.
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Gets the user name used by the cache.
   *
   * @return User name
   */
  public String getCacheUserName() {
    return cacheUserName;
  }

  /**
   * Sets the user name used by the cache.
   *
   * @param cacheUserName User name
   */
  public void setCacheUserName(String cacheUserName) {
    this.cacheUserName = cacheUserName;
  }

  /**
   * Gets the password used by the cache.
   *
   * @return Password
   */
  public String getCacheUserPassword() {
    return cacheUserPassword;
  }

  /**
   * Sets the password used by the cache.
   *
   * @param cacheUserPassword Password
   */
  public void setCacheUserPassword(String cacheUserPassword) {
    this.cacheUserPassword = cacheUserPassword;
  }


  /**
   * Create and populate a RepositoryConfiguration based on the contens of this config command instance.
   *
   * @return New, populated RepositoryConfiguration
   */
  public RepositoryConfiguration createRepositoryConfiguration() {
    final RepositoryConfiguration configuration = new RepositoryConfiguration(getName());
    BeanUtils.copyProperties(this, configuration);

    if (AccessMethod.USER == accessMethod) {
      configuration.setEnableAccessControl(true);
      configuration.setUserCredentials(Credentials.EMPTY);
    } else {
      configuration.setUserCredentials(new Credentials(userName, userPassword));
    }

    if (isCacheUsed()) {
      configuration.setCacheCredentials(new Credentials(cacheUserName, cacheUserPassword));
    } else {
      configuration.setCacheCredentials(Credentials.EMPTY);
    }
    return configuration;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
