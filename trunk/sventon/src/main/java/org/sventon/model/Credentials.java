/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * Credentials.
 *
 * @author jesper@sventon.org
 */
public final class Credentials implements Serializable {

  private static final long serialVersionUID = -30885545779723155L;

  /**
   * Represents empty/blank credentials.
   */
  public static final Credentials EMPTY = new Credentials("", "");

  /**
   * User name.
   */
  private final String userName;

  /**
   * Password.
   */
  private final String password;

  /**
   * Constructor.
   *
   * @param userName User name.
   * @param password Password.
   */
  public Credentials(final String userName, final String password) {
    this.userName = encodeBase64(userName);
    this.password = encodeBase64(password);
  }

  /**
   * Gets the user name.
   *
   * @return User name
   */
  public String getUserName() {
    return decodeBase64(userName);
  }

  /**
   * Gets the password.
   *
   * @return Password
   */
  public String getPassword() {
    return decodeBase64(password);
  }

  /**
   * @return True if credentials are empty/blank/null.
   */
  public boolean isEmpty() {
    return StringUtils.isEmpty(userName) && StringUtils.isEmpty(password);
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
        append("userName", userName != null ? "*****" : "<null>").
        append("password", password != null ? "*****" : "<null>").
        toString();
  }
}
