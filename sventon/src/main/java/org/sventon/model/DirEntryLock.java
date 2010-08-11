package org.sventon.model;

import java.util.Date;

/**
 * Represents a Subversion directory entry lock.
 *
 * @author jesper@sventon.org
 */
public class DirEntryLock {

  private final String id;
  private final String path;
  private final String owner;
  private final String comment;
  private final Date creationDate;
  private final Date expirationDate;

  /**
   * Constructor.
   *
   * @param id
   * @param path
   * @param owner
   * @param comment
   * @param creationDate
   * @param expirationDate
   */
  public DirEntryLock(String id, String path, String owner, String comment, Date creationDate, Date expirationDate) {
    this.id = id;
    this.path = path;
    this.owner = owner;
    this.comment = comment;
    this.creationDate = creationDate;
    this.expirationDate = expirationDate;
  }

  public String getId() {
    return id;
  }

  public String getPath() {
    return path;
  }

  public String getOwner() {
    return owner;
  }

  public String getComment() {
    return comment;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public Date getExpirationDate() {
    return expirationDate;
  }


}
