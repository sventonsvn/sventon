/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
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

import java.io.Serializable;
import java.util.*;

/**
 * LogMessageSearchItem.
 *
 * @author jesper@sventon.org
 */
@Searchable(root = true)
public final class LogMessageSearchItem implements Serializable {

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
  private final List<String> paths = new ArrayList<String>();


  /**
   * Default constructor.
   */
  protected LogMessageSearchItem() {
  }

  /**
   * Constructor.
   *
   * @param logEntry SVN log entry
   */
  public LogMessageSearchItem(final LogEntry logEntry) {
    this.revision = logEntry.getRevision();
    this.author = logEntry.getAuthor();
    this.date = logEntry.getDate() != null ? (Date) logEntry.getDate().clone() : null;
    this.message = logEntry.getMessage();
    this.paths.addAll(toStringList(logEntry.getChangedPaths()));
  }

  /**
   * @param changedPaths Paths
   * @return List of path strings
   */
  public static List<String> toStringList(final SortedSet<ChangedPath> changedPaths) {
    if (changedPaths == null) return Collections.emptyList();
    final List<String> paths = new ArrayList<String>();
    for (ChangedPath changedPath : changedPaths) {
      paths.add(changedPath.getPath());
    }
    return paths;
  }

  /**
   * Gets the revision for this log entry.
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
   * Used to apply style to the log message string.
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
   * Used to apply style to the author string.
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
   * @return Unmodifiable list of changed paths in this revision.
   */
  public List<String> getPaths() {
    return Collections.unmodifiableList(paths);
  }
}
