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

import de.berlios.sventon.appl.InstanceConfiguration;
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

  private String name;
  private String repositoryUrl;
  private String uid;
  private String pwd;
  private String connectionTestUid;
  private String connectionTestPwd;
  private boolean useCache;
  private boolean zipDownloadsAllowed;
  private boolean enableAccessControl;

  public String getRepositoryUrl() {
    return repositoryUrl;
  }

  public void setRepositoryUrl(final String repositoryUrl) {
    this.repositoryUrl = repositoryUrl;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(final String uid) {
    this.uid = uid;
  }

  public String getPwd() {
    return pwd;
  }

  public void setPwd(final String pwd) {
    this.pwd = pwd;
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

  public String getConnectionTestUid() {
    return connectionTestUid;
  }

  public void setConnectionTestUid(final String connectionTestUid) {
    this.connectionTestUid = connectionTestUid;
  }

  public String getConnectionTestPwd() {
    return connectionTestPwd;
  }

  public void setConnectionTestPwd(final String connectionTestPwd) {
    this.connectionTestPwd = connectionTestPwd;
  }

  /**
   * Create an populate a InstanceConfiguration based on the contens of this config command instance.
   *
   * @return New, populated InstanceConfiguration
   */
  public InstanceConfiguration createInstanceConfiguration() {
    InstanceConfiguration configuration = new InstanceConfiguration(getName());
    BeanUtils.copyProperties(this, configuration);
    return configuration;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
  }

}
