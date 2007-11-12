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
package de.berlios.sventon.web.command;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * ConfigCommand.
 * <p/>
 * Command class used to bind and pass servlet parameter arguments for sventon configuration.
 *
 * @author jesper@users.berlios.de
 * @author patrik@sventon.org
 */
public final class ConfigCommand {

  private String name;
  private String repositoryURL;
  private String username;
  private String password;
  private String connectionTestUsername;
  private String connectionTestPassword;
  private boolean useCache;
  private boolean zipDownloadsAllowed;
  private boolean enableAccessControl;

  public String getRepositoryURL() {
    return repositoryURL;
  }

  public void setRepositoryURL(final String repositoryURL) {
    this.repositoryURL = repositoryURL;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public boolean isCacheUsed() {
    return useCache;
  }

  public void setCacheUsed(final boolean useCache) {
    this.useCache = useCache;
  }

  public boolean isEnableAccessControl() {
    return enableAccessControl;
  }

  public void setEnableAccessControl(final boolean enableAccessControl) {
    this.enableAccessControl = enableAccessControl;
  }

  public boolean isZippedDownloadsAllowed() {
    return zipDownloadsAllowed;
  }

  public void setZippedDownloadsAllowed(final boolean zipDownloadsAllowed) {
    this.zipDownloadsAllowed = zipDownloadsAllowed;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getConnectionTestUsername() {
    return connectionTestUsername;
  }

  public void setConnectionTestUsername(final String connectionTestUsername) {
    this.connectionTestUsername = connectionTestUsername;
  }

  public String getConnectionTestPassword() {
    return connectionTestPassword;
  }

  public void setConnectionTestPassword(final String connectionTestPassword) {
    this.connectionTestPassword = connectionTestPassword;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
  }

}
