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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * Represents a pegged repository entry.
 *
 * @author jesper@sventon.org
 */
public class PeggedDirEntry implements Serializable {

  private static final long serialVersionUID = 3629810826053736323L;

  /**
   * The wrapped repository entry
   */
  private final DirEntry entry;

  /**
   * Peg revision.
   */
  private final long pegRevision;

  /**
   * Constructor.
   *
   * @param entry       The <code>DirEntry</code>.
   * @param pegRevision The peg revision.
   */
  public PeggedDirEntry(final DirEntry entry, final long pegRevision) {
    this.entry = entry;
    this.pegRevision = pegRevision;
  }

  /**
   * Gets the peg revision.
   *
   * @return Peg revision.
   */
  public long getPegRevision() {
    return pegRevision;
  }

  /**
   * Gets the pegged entry.
   *
   * @return Entry
   */
  public DirEntry getEntry() {
    return entry;
  }

  /**
   * @return String representation of this object.
   */
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}
