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

import org.apache.commons.lang.StringEscapeUtils;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.sventon.util.WebUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * LogEntryWrapper.
 *
 * @author patrik@sventon.org
 * @author jesper@sventon.org
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
   * Constructor.
   *
   * @param logEntry The log entry
   */
  public LogEntryWrapper(final SVNLogEntry logEntry) {
    svnLogEntry = logEntry;
    this.pathAtRevision = null;
  }

  /**
   * Contructor.
   *
   * @param logEntry       The log entry
   * @param pathAtRevision The entry's path at given revision.
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

  /**
   * @return Map of the changed paths.
   */
  public Map<String, SVNLogEntryPath> getChangedPaths() {
    //noinspection unchecked
    return svnLogEntry.getChangedPaths();
  }

  /**
   * @return Revision number.
   */
  public long getRevision() {
    return svnLogEntry.getRevision();
  }

  /**
   * @return Author.
   */
  public String getAuthor() {
    return svnLogEntry.getAuthor();
  }

  /**
   * @return Log entry date.
   */
  public Date getDate() {
    return svnLogEntry.getDate();
  }

  /**
   * @return The log message.
   */
  public String getMessage() {
    return svnLogEntry.getMessage();
  }

  /**
   * Gets the log message formatted for display on the web.
   * XML characters will be escaped and new lines will be translated into
   * the HTML equivalent.
   *
   * @return Web format log message.
   */
  public String getWebFormattedMessage() {
    return WebUtils.nl2br(StringEscapeUtils.escapeXml(svnLogEntry.getMessage()));
  }

  /**
   * Creates a list of wrappers based on given list of log entries.
   *
   * @param logEntries SVN Log entries
   * @return List of wrappers
   */
  public static List<LogEntryWrapper> convert(List<SVNLogEntry> logEntries) {
    final List<LogEntryWrapper> wrappers = new ArrayList<LogEntryWrapper>();
    for (SVNLogEntry logEntry : logEntries) {
      wrappers.add(new LogEntryWrapper(logEntry));
    }
    return wrappers;
  }

}
