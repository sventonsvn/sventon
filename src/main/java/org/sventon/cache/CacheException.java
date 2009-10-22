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
package org.sventon.cache;

import org.sventon.SventonException;

/**
 * Exception thrown by <code>Cache</code>.
 *
 * @author jesper@sventon.org
 */
public class CacheException extends SventonException {

  private static final long serialVersionUID = 7044111668643292134L;

  /**
   * Constructor.
   *
   * @param message Exception message.
   */
  public CacheException(final String message) {
    super(message);
  }

  /**
   * Constructor.
   *
   * @param message Exception message.
   * @param cause   Cause
   */
  public CacheException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
