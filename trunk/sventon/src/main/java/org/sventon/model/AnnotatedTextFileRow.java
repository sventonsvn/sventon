/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.model;

import java.util.Date;

/**
 * Represents a row of an annotated file.
 *
 * @author jesper@sventon.org
 */
public final class AnnotatedTextFileRow {

  /**
   * Time stamp.
   */
  private final Date date;

  /**
   * Revision.
   */
  private final long revision;

  /**
   * Author.
   */
  private final String author;

  /**
   * The row number.
   */
  private final int rowNumber;

  /**
   * The line content.
   */
  private String content;

  /**
   * Constructor.
   *
   * @param date      Date when changes including this line were commited to the repository.
   * @param revision  The revision the changes were commited to.
   * @param author    The person who did the changes.
   * @param rowNumber Row number.
   * @param content   The line content.
   */
  public AnnotatedTextFileRow(final Date date, final long revision, final String author,
                              final int rowNumber, final String content) {
    this.date = date;
    this.revision = revision;
    this.author = author;
    this.rowNumber = rowNumber;
    this.content = content;
  }

  /**
   * Gets the author.
   *
   * @return The author.
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Gets the date.
   *
   * @return The date.
   */
  public Date getDate() {
    return date;
  }

  /**
   * Gets the revision.
   *
   * @return The revision.
   */
  public long getRevision() {
    return revision;
  }

  /**
   * Gets the row number of this file row.
   *
   * @return Row number
   */
  public int getRowNumber() {
    return rowNumber;
  }

  /**
   * Gets the line content.
   *
   * @return Line content.
   */
  public String getContent() {
    return content;
  }

  /**
   * Sets the line content.
   *
   * @param content Line content
   */
  public void setContent(final String content) {
    this.content = content;
  }
}
