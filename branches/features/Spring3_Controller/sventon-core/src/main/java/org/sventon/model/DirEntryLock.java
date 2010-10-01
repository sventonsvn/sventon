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

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a Subversion directory entry lock.
 *
 * @author jesper@sventon.org
 */
public class DirEntryLock implements Serializable {

  private static final long serialVersionUID = -3679696499450455562L;

  private final String id;
  private final String path;
  private final String owner;
  private final String comment;
  private final Date creationDate;
  private final Date expirationDate;

  /**
   * Constructor.
   *
   * @param id             Id
   * @param path           Locked path
   * @param owner          Lock owner
   * @param comment        Comment
   * @param creationDate   Lock creation date
   * @param expirationDate Lock expire date
   */
  public DirEntryLock(String id, String path, String owner, String comment, Date creationDate, Date expirationDate) {
    this.id = id;
    this.path = path;
    this.owner = owner;
    this.comment = comment;
    this.creationDate = creationDate;
    this.expirationDate = expirationDate;
  }

  /**
   * @return Id
   */
  public String getId() {
    return id;
  }

  /**
   * @return Locked path
   */
  public String getPath() {
    return path;
  }

  /**
   * @return Lock owner
   */
  public String getOwner() {
    return owner;
  }

  /**
   * @return Comment
   */
  public String getComment() {
    return comment;
  }

  /**
   * @return The locks creation date
   */
  public Date getCreationDate() {
    return creationDate;
  }

  /**
   * @return The locks expire date
   */
  public Date getExpirationDate() {
    return expirationDate;
  }


  @Override
  public String toString() {
    return "Lock [" + id + "] on [" + path + "] owned by [" + owner + "] created [" + creationDate + "]. Comment: " + comment;
  }
}
