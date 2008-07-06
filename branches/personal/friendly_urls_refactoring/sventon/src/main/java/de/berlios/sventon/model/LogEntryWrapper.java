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
package de.berlios.sventon.model;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import java.util.Date;
import java.util.Map;

/**
 * LogEntryWrapper.
 *
 * @author patrikfr@users.berlios.de
 */
public final class LogEntryWrapper {

  /**
   * Subversion log entry.
   */
  private final SVNLogEntry svnLogEntry;

  /**
   * The path at revision.
   */
  private final String pathAtRevision;

  /**
   * @param logEntry       The log entry
   * @param pathAtRevision The path
   */
  public LogEntryWrapper(final SVNLogEntry logEntry, final String pathAtRevision) {
    svnLogEntry = logEntry;
    this.pathAtRevision = pathAtRevision;
  }

  /**
   * Gets the path at revision.
   *
   * @return Returns the pathAtRevision.
   */
  public String getPathAtRevision() {
    return pathAtRevision;
  }

  public Map<String, SVNLogEntryPath> getChangedPaths() {
    //noinspection unchecked
    return svnLogEntry.getChangedPaths();
  }

  public long getRevision() {
    return svnLogEntry.getRevision();
  }

  public String getAuthor() {
    return svnLogEntry.getAuthor();
  }

  public Date getDate() {
    return svnLogEntry.getDate();
  }

  public String getMessage() {
    return svnLogEntry.getMessage();
  }

}
