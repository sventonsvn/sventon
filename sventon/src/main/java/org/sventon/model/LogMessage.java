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

import org.compass.annotations.Index;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * LogMessage.
 *
 * @author jesper@sventon.org
 */
@Searchable(root = true)
public final class LogMessage implements Serializable {

  public static final String PATHS_DELIMITER = "\0";

  public static final String NOT_AVAILABLE_TAG = "_NA_";

  private static final long serialVersionUID = 1484899822981422330L;

  @SearchableId
  private long revision;

  @SearchableProperty(nullValue = NOT_AVAILABLE_TAG)
  private String author;

  @SearchableProperty
  private Date date;

  @SearchableProperty
  private String message;

  @SearchableProperty(index = Index.NOT_ANALYZED)
  private String paths;

  /**
   * Default constructor.
   */
  protected LogMessage() {
  }

  /**
   * Constructor.
   *
   * @param svnLogEntry SVN log entry
   */
  public LogMessage(final SVNLogEntry svnLogEntry) {
    this.revision = svnLogEntry.getRevision();
    this.author = svnLogEntry.getAuthor();
    this.date = svnLogEntry.getDate() != null ? (Date) svnLogEntry.getDate().clone() : null;
    this.message = svnLogEntry.getMessage();
    this.paths = extractAndConcatinatePaths(svnLogEntry.getChangedPaths());
  }

  /**
   * Concatinates given map of paths using {@link #PATHS_DELIMITER} as separator.
   *
   * @param changedPaths Changed paths for a certain revision.
   * @return Concatinated string of paths, or null/empty string if no changed paths in revision.
   */
  private String extractAndConcatinatePaths(Map changedPaths) {
    if (changedPaths != null) {
      StringBuilder pathsStringBuilder = new StringBuilder();
      for (Object path : changedPaths.keySet()) {
        pathsStringBuilder.append(PATHS_DELIMITER).append(path);
      }
      return pathsStringBuilder.toString();
    } else {
      return null;
    }
  }

  /**
   * Gets the revision for this log message.
   *
   * @return The revision
   */
  public long getRevision() {
    return revision;
  }

  /**
   * Gets the log message.
   *
   * @return The message.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the message.
   *
   * @param message Message.
   */
  public void setMessage(final String message) {
    this.message = message;
  }

  /**
   * Gets the author.
   *
   * @return Author
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Sets the author.
   *
   * @param author Author.
   */
  public void setAuthor(final String author) {
    this.author = author;
  }

  /**
   * Gets the date.
   *
   * @return The date
   */
  public Date getDate() {
    return date != null ? (Date) date.clone() : null;
  }

  /**
   * Gets the paths.
   *
   * @return The paths.
   */
  public String getPaths() {
    return paths;
  }

  /**
   * Sets the paths.
   *
   * @param paths Paths.
   */
  public void setPaths(final String paths) {
    this.paths = paths;
  }

}
