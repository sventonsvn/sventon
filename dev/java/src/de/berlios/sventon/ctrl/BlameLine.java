package de.berlios.sventon.ctrl;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.Date;

/**
 * Wrapper class for blame lines.
 * Contains information about who added the line to the file
 * and what revision it was done at.
 * @author patrikfr@users.berlios.de
 */
public class BlameLine {

  /** Time stamp. */
  private final Date date;

  /** Revision. */
  private final long revision;

  /** Author. */
  private final String author;

  /** Source line. */
  private final String line;

  /**
   * @param date Date when changes including this line were commited to the repository.
   * @param revision The revision the changes were commited to.
   * @param author The person who did the changes.
   * @param line A single file line that is a part of those changes.
   */
  public BlameLine(final Date date, final long revision, final String author, final String line) {
    this.date = date;
    this.revision = revision;
    this.author = author;
    this.line = line;
  }
  
  /**
   * @return The author.
   */
  public final String getAuthor() {
    return author;
  }

  /**
   * @return The date.
   */
  public final Date getDate() {
    return date;
  }

  /**
   * @return The line.
   */
  public final String getLine() {
    return line;
  }

  /**
   * @return The revision.
   */
  public final long getRevision() {
    return revision;
  }

  /**
   * @return String representation of this object.
   */
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
  }

}
