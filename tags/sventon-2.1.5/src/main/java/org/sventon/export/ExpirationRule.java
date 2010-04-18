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
package org.sventon.export;

import java.io.File;

/**
 * File expiration rule.
 *
 * @author jesper@sventon.org
 */
public interface ExpirationRule {

  /**
   * Returns true if given file has expired.
   *
   * @param file File
   * @return True if file has expired according to the rules, false if not.
   */
  boolean hasExpired(final File file);

}