/*
 * ====================================================================
 * Copyright (c) 2005 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.command;

/**
 * ConfigCommand.
 * <p>
 * Command class used to bind and pass servlet parameter arguments for sventon configuration.
 *
 * @author jesper@users.berlios.de
 */
public class ConfigCommand {

  private String repositoryURL;
  private String username;
  private String password;
  private String currentDir;
  private String configPath;
  private String mountPoint;

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

  public void setCurrentDir(final String path) {
    this.currentDir = path;
  }

  public String getCurrentDir() {
    return this.currentDir;
  }

  public void setConfigPath(final String path) {
    this.configPath = path;
  }

  public String getConfigPath() {
    return this.configPath;
  }

  public String getMountPoint() {
    return mountPoint;
  }

  public void setMountPoint(String mountPoint) {
    this.mountPoint = mountPoint;
  }
}
