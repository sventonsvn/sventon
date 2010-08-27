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
import org.apache.commons.lang.StringUtils;
import org.sventon.util.DateUtil;
import org.sventon.util.WebUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * LogEntry.
 *
 * @author patrik@sventon.org
 * @author jesper@sventon.org
 */
public final class LogEntry implements Serializable {

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
  private final Set<ChangedPath> changedPaths;

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
  public LogEntry(final long revision, final Map<RevisionProperty, String> properties, final Set<ChangedPath> changedPaths) {
    this.revision = revision;
    this.properties = properties == null ? Collections.EMPTY_MAP : properties;
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
  public Set<ChangedPath> getChangedPaths() {
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
   * Gets the log message formatted for display on the web.
   * XML characters will be escaped and new lines will be translated into
   * the HTML equivalent.
   *
   * @return Web format log message.
   */
  public String getWebFormattedMessage() {
    return WebUtils.nl2br(StringEscapeUtils.escapeXml(getMessage()));
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
