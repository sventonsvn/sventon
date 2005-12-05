/*
 * ====================================================================
 * Copyright (c) 2005 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.blame;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.Date;

/**
 * Wrapper class for blame lines.
 * Contains information about who added the line to the file
 * and what revision it was done at.
 * 
 * @author patrikfr@users.berlios.de
 */
public class BlameLine {

  /** Time stamp. */
  private final Date date;

  /** Revision. */
  private final long revision;

  /** Author. */
  private final String author;

  /**
   * @param date Date when changes including this line were commited to the repository.
   * @param revision The revision the changes were commited to.
   * @param author The person who did the changes.
   */
  public BlameLine(final Date date, final long revision, final String author) {
    this.date = date;
    this.revision = revision;
    this.author = author;
  }
  
  /**
   * Gets the author.
   * @return The author.
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Gets the date.
   * @return The date.
   */
  public Date getDate() {
    return date;
  }

  /**
   * Gets the revision.
   * @return The revision.
   */
  public long getRevision() {
    return revision;
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
  }

}
