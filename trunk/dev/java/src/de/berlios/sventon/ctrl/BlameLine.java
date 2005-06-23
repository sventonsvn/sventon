package de.berlios.sventon.ctrl;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author patrikfr@users.berlios.de
 *
 */
public class BlameLine {
  private final Date date;

  private final long revision;

  private final String author;

  private final String line;

  /**
   * @param date
   * @param revision
   * @param author
   * @param line
   */
  public BlameLine(final Date date, final long revision, final String author, final String line) {
    super();
    this.date = date;
    this.revision = revision;
    this.author = author;
    this.line = line;
  }
  
  
  
  public final String getAuthor() {
    return author;
  }



  public final Date getDate() {
    return date;
  }



  public final String getLine() {
    return line;
  }



  public final long getRevision() {
    return revision;
  }



  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
  }

}
