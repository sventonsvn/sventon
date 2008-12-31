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
package org.sventon.model;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Credentials.
 *
 * @author jesper@sventon.org
 */
public class Credentials {

  /**
   * Username.
   */
  private final String username;

  /**
   * Password.
   */
  private final String password;


  /**
   * Constructor.
   *
   * @param username Username.
   * @param password Password.
   */
  public Credentials(final String username, final String password) {
    this.username = encodeBase64(username);
    this.password = encodeBase64(password);
  }

  /**
   * Gets the username.
   *
   * @return Username
   */
  public String getUsername() {
    return decodeBase64(username);
  }

  /**
   * Gets the password.
   *
   * @return Password
   */
  public String getPassword() {
    return decodeBase64(password);
  }

  private String encodeBase64(final String string) {
    return (string != null) ? new String(Base64.encodeBase64(string.getBytes())) : null;
  }

  private String decodeBase64(final String string) {
    return (string != null) ? new String(Base64.decodeBase64(string.getBytes())) : null;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
        append("username", username != null ? "*****" : "<null>").
        append("password", password != null ? "*****" : "<null>").
        toString();
  }
}
