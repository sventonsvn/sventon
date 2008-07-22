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
package org.sventon.diff;

/**
 * Exception thrown to indicate that a given files are identical.
 *
 * @author jesper@users.berlios.de
 */
public final class IdenticalFilesException extends DiffException {

  private static final long serialVersionUID = 5879577101437011341L;

  /**
   * Constructor.
   *
   * @param message Exception message text
   */
  public IdenticalFilesException(final String message) {
    super(message);
  }

}
