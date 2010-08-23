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

/**
 * Represents a changed path in a revision change set.
 *
 * @author jesper@sventon.org
 * @author jorgen@sventon.org
 */
public class ChangedPath implements Serializable, Comparable<ChangedPath> {

  private final String path;

  private final String copyPath;

  private final long copyRevision;

  private final ChangeType type;

  /**
   * Constructor.
   *
   * @param path         The path that was changed in the revision.
   * @param copyPath     The original location this entry was copied from. May be null.
   * @param copyRevision The original revision this entry was copied from. May be null.
   * @param type         Type of change.
   */
  public ChangedPath(String path, String copyPath, long copyRevision, ChangeType type) {
    this.path = path;
    this.copyPath = copyPath;
    this.copyRevision = copyRevision;
    this.type = type;
  }

  /**
   * @return Type of change
   */
  public ChangeType getType() {
    return type;
  }

  /**
   * @return The changed path
   */
  public String getPath() {
    return path;
  }

  /**
   * @return Original path the entry was copied from
   */
  public String getCopyPath() {
    return copyPath;
  }

  /**
   * @return Original revision the entry was copied from
   */
  public long getCopyRevision() {
    return copyRevision;
  }

  @Override
  public int compareTo(ChangedPath that) {
    return getPath().compareTo(that.getPath());
  }
}
