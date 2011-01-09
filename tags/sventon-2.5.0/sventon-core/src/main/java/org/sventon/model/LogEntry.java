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
package org.sventon.model;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * LogEntry.
 *
 * @author patrik@sventon.org
 * @author jesper@sventon.org
 */
public final class LogEntry implements Serializable {

  private static final long serialVersionUID = 1625655173694918781L;

  /**
   * The path at revision.
   */
  private String pathAtRevision;

  /**
   * Revision.
   */
  private final long revision;

  /**
   * Changed paths.
   */
  private final SortedSet<ChangedPath> changedPaths;

  /**
   * Map holding the RevisionProperties and the corresponding (String) value.
   */
  private final Map<RevisionProperty, String> properties;

  /**
   * Constructor.
   *
   * @param revision     Revision
   * @param properties   Properties
   * @param changedPaths Changed paths
   */
  public LogEntry(final long revision, final Map<RevisionProperty, String> properties, final SortedSet<ChangedPath> changedPaths) {
    this.revision = revision;
    this.properties = properties == null ? Collections.<RevisionProperty, String>emptyMap() : properties;
    this.changedPaths = changedPaths;
  }

  public void setPathAtRevision(String path) {
    pathAtRevision = path;
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
  public SortedSet<ChangedPath> getChangedPaths() {
    //noinspection unchecked
    return changedPaths;
  }

  /**
   * @return Revision number.
   */
  public long getRevision() {
    return revision;
  }

  /**
   * @return Author.
   */
  public String getAuthor() {
    return properties.get(RevisionProperty.AUTHOR);
  }

  /**
   * @return Log entry date.
   */
  public Date getDate() {
    final String date = properties.get(RevisionProperty.DATE);
    if (StringUtils.isEmpty(date)) return null;
    return DateUtil.parseISO8601(date);
  }

  /**
   * @return The log message.
   */
  public String getMessage() {
    return properties.get(RevisionProperty.LOG);
  }

  /**
   * Iterate through (and modify!) the log entries and set the pathAtRevision for each entry.
   *
   * @param logEntries the entries to set pathAtRevision for
   * @param path       the starting path
   */
  public static void setPathAtRevisionInLogEntries(final List<LogEntry> logEntries, final String path) {
    String pathAtRevision = path;

    for (final LogEntry logEntry : logEntries) {
      logEntry.setPathAtRevision(pathAtRevision);

      //noinspection unchecked
      final SortedSet<ChangedPath> allChangedPaths = logEntry.getChangedPaths();
      if (allChangedPaths != null) {
        for (ChangedPath entryPath : allChangedPaths) {
          if (entryPath.getCopyPath() != null) {
            int i = StringUtils.indexOfDifference(entryPath.getPath(), pathAtRevision);
            if (i == -1) { // Same path
              pathAtRevision = entryPath.getCopyPath();
            } else if (entryPath.getPath().length() == i) { // Part path, can be a branch
              pathAtRevision = entryPath.getCopyPath() + pathAtRevision.substring(i);
            } else {
              // TODO: else what? Is this OK to let the path be the previous?
            }
          }
        }
      }
    }
  }

  /**
   * Checks if given log entry contains accessible information, i.e. it was
   * fetched from the repository by a user with access to the affected paths.
   *
   * @return True if accessible, false if not.
   */
  public boolean isAccessible() {
    return getDate() != null
        && (getChangedPaths() != null
        && !getChangedPaths().isEmpty());
  }

  @Override
  public String toString() {
    return "LogEntry{" +
        "pathAtRevision='" + pathAtRevision + '\'' +
        ", revision=" + revision +
        ", changedPaths=" + changedPaths +
        ", properties=" + properties +
        '}';
  }
}
