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
package de.berlios.sventon.model;

import org.tmatesoft.svn.core.SVNLogEntry;

/**
 * LogEntryBundle
 *
 * @author patrikfr@users.berlios.de
 */
public class LogEntryBundle {

  /**
   * Subversion log entry.
   */
  private SVNLogEntry svnLogEntry;

  /**
   * The path at revision.
   */
  private String pathAtRevision;

  /**
   * @param logEntry       The log entry
   * @param pathAtRevision The path
   */
  public LogEntryBundle(final SVNLogEntry logEntry, final String pathAtRevision) {
    svnLogEntry = logEntry;
    this.pathAtRevision = pathAtRevision;
  }

  /**
   * @return Returns the pathAtRevision.
   */
  public String getPathAtRevision() {
    return pathAtRevision;
  }

  /**
   * @return Returns the svnLogEntry.
   */
  public SVNLogEntry getSvnLogEntry() {
    return svnLogEntry;
  }

}
