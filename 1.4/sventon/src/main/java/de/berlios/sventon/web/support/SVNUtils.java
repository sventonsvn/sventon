/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.support;

import org.tmatesoft.svn.core.SVNLogEntry;

/**
 * Misc SVNKit utilities.
 *
 * @author jesper@users.berlios.de
 */
public final class SVNUtils {

  /**
   * Private.
   */
  private SVNUtils() {
  }

  /**
   * Checks if given log entry contains accessible information, i.e. it was
   * fetched from the repository by a user with access to the affected paths.
   *
   * @param logEntry Log entry.
   * @return True if accessible, false if not.
   */
  public static boolean isAccessible(final SVNLogEntry logEntry) {
    return logEntry != null
        && logEntry.getDate() != null
        && (logEntry.getChangedPaths() != null
        && !logEntry.getChangedPaths().isEmpty());
  }
}
