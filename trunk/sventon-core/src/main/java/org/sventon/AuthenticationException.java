/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon;

/**
 * AuthenticationException.
 *
 * @author jesper@sventon.org
 */
public class AuthenticationException extends SventonException {

  /**
   * Constructor.
   *
   * @param message Exception message.
   */
  public AuthenticationException(String message) {
    super(message);
  }

  /**
   * Constructor.
   *
   * @param message Exception message.
   * @param cause   Cause.
   */
  public AuthenticationException(String message, Exception cause) {
    super(message, cause);
  }

}
