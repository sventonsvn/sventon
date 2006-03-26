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
package de.berlios.sventon.command;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * ConfigCommand.
 * <p/>
 * Command class used to bind and pass servlet parameter arguments for sventon configuration.
 *
 * @author jesper@users.berlios.de
 */
public class ConfigCommand {

  private String repositoryURL;
  private String username;
  private String password;
  private String configPath;
  private boolean useIndex;

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

  public void setConfigPath(final String path) {
    this.configPath = path;
  }

  public String getConfigPath() {
    return this.configPath;
  }

  public void setIndexUsed(final boolean useIndex) {
    this.useIndex = useIndex;
  }

  public boolean isIndexUsed() {
    return this.useIndex;
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
  }

}
