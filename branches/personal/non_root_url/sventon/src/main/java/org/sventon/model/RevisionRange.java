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

import java.io.Serializable;

/**
 * Represents a range of revisions.
 *
 * @author jesper@sventon.org
 */
public class RevisionRange implements Serializable {

  private static final long serialVersionUID = 5347661656582322478L;

  /**
   * From revision.
   */
  private final long fromRevision;

  /**
   * To revision.
   */
  private final long toRevision;

  /**
   * Constructor.
   *
   * @param fromRevision From revision
   * @param toRevision   To revision
   */
  public RevisionRange(final long fromRevision, final long toRevision) {
    this.fromRevision = fromRevision;
    this.toRevision = toRevision;
  }

  /**
   * Gets the to revision.
   *
   * @return To revision.
   */
  public long getFromRevision() {
    return fromRevision;
  }

  /**
   * Gets the from revision.
   *
   * @return From revision.
   */
  public long getToRevision() {
    return toRevision;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RevisionRange that = (RevisionRange) o;

    if (fromRevision != that.fromRevision) return false;
    if (toRevision != that.toRevision) return false;

    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result;
    result = (int) (fromRevision ^ (fromRevision >>> 32));
    result = 31 * result + (int) (toRevision ^ (toRevision >>> 32));
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return fromRevision + "-" + toRevision;
  }
}
