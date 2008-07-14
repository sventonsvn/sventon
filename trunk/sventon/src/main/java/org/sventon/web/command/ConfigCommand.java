/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.command;

import org.sventon.appl.RepositoryConfiguration;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.beans.BeanUtils;

/**
 * ConfigCommand.
 * <p/>
 * Command class used to bind and pass servlet parameter arguments for sventon configuration.
 *
 * @author jesper@users.berlios.de
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
     * Global access, one uid/pwd for the entire repos, set globally in sventon, transparent for sventon user.
     */
    GLOBAL(),

    /**
     * User access, each sventon user needs supply it's own uid/pwd for accessing restricted parts of the repository.
     */
    USER()
  }

  private String name;
  private String repositoryUrl;
  private String uid;
  private String pwd;
  private String connectionTestUid;
  private String connectionTestPwd;
  private boolean useCache;
  private boolean zipDownloadsAllowed;
  private ConfigCommand.AccessMethod accessMethod = AccessMethod.ANONYMOUS;

  /**
   * Gets the repository URL.
   *
   * @return URL.
   */
  public String getRepositoryUrl() {
    return repositoryUrl;
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
   * Gets the UID.
   *
   * @return UID.
   */
  public String getUid() {
    return uid;
  }

  /**
   * Sets the UID.
   *
   * @param uid UID.
   */
  public void setUid(final String uid) {
    this.uid = uid;
  }

  /**
   * Gets the password.
   *
   * @return Password.
   */
  public String getPwd() {
    return pwd;
  }

  /**
   * Sets the password.
   *
   * @param pwd Password.
   */
  public void setPwd(final String pwd) {
    this.pwd = pwd;
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
   * Gets the connection test UID.
   *
   * @return Test UID.
   */
  public String getConnectionTestUid() {
    return connectionTestUid;
  }

  /**
   * Sets the connection test UID.
   *
   * @param connectionTestUid Test UID.
   */
  public void setConnectionTestUid(final String connectionTestUid) {
    this.connectionTestUid = connectionTestUid;
  }

  /**
   * Gets the connection test password.
   *
   * @return Test password.
   */
  public String getConnectionTestPwd() {
    return connectionTestPwd;
  }

  /**
   * Sets the connection test password.
   *
   * @param connectionTestPwd Test password.
   */
  public void setConnectionTestPwd(final String connectionTestPwd) {
    this.connectionTestPwd = connectionTestPwd;
  }

  /**
   * Create an populate a RepositoryConfiguration based on the contens of this config command instance.
   *
   * @return New, populated RepositoryConfiguration
   */
  public RepositoryConfiguration createRepositoryConfiguration() {
    final RepositoryConfiguration configuration = new RepositoryConfiguration(getName());
    BeanUtils.copyProperties(this, configuration);
    configuration.setEnableAccessControl(accessMethod == AccessMethod.USER);
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
