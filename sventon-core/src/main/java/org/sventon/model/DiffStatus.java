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

/**
 *
 */
public class DiffStatus {
  private final StatusType modificationType;
  private final SVNURL url;
  private final String path;
  private final boolean propertyModified;

  public DiffStatus(StatusType modificationType, SVNURL url, String path, boolean propertyModified) {
    this.modificationType = modificationType;
    this.url = url;
    this.path = path;
    this.propertyModified = propertyModified;
  }


  /**
   * Returns the type of modification for the current
   * item.
   *
   * @return a path change type
   */
  public StatusType getModificationType() {
    return modificationType;
  }


  /**
   * Url of the item.
   *
   * @return item url
   */
  public SVNURL getUrl() {
    return url;
  }

  /**
   * Returns a relative path of the item.
   * Set for Working Copy items and relative to the anchor of diff status operation.
   *
   * @return item path
   */
  public String getPath() {
    return path;
  }

  /**
   * Says whether properties of the Working Copy item are modified.
   *
   * @return <span class="javakeyword">true</span> if properties were modified
   *         in a particular revision, <span class="javakeyword">false</span>
   *         otherwise
   */
  public boolean isPropertyModified() {
    return propertyModified;
  }
}
