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

import java.io.Serializable;

/**
 * RevisionProperty.
 */
public final class RevisionProperty implements Serializable {

  /**
   * An <span class="javastring">"svn:author"</span> revision
   * property (that holds the name of the revision's author).
   */
  public static final RevisionProperty AUTHOR = new RevisionProperty("svn:author");

  /**
   * An <span class="javastring">"svn:log"</span> revision property -
   * the one that stores a log message attached to a revision
   * during a commit operation.
   */
  public static final RevisionProperty LOG = new RevisionProperty("svn:log");

  /**
   * An <span class="javastring">"svn:date"</span> revision property
   * that is a date & time stamp representing the time when the
   * revision was created.
   */
  public static final RevisionProperty DATE = new RevisionProperty("svn:date");


  /**
   * List of predefined revision properties.
   */
  private static final RevisionProperty[] PRE_DEFINED_PROP = new RevisionProperty[]{AUTHOR, LOG, DATE};

  /**
   * Property name.
   */
  private final String name;
  private static final long serialVersionUID = -6565473731138429326L;

  /**
   * Constructor.
   *
   * @param name Name of property
   */
  private RevisionProperty(final String name) {
    this.name = name;
  }

  /**
   * @return Property name
   */
  public String getName() {
    return name;
  }

  public static RevisionProperty byName(String revisionPropertyName) {
    for (RevisionProperty revisionProperty : PRE_DEFINED_PROP) {
      if (revisionProperty.getName().equals(revisionPropertyName)) {
        return revisionProperty;
      }
    }

    return new RevisionProperty(revisionPropertyName);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RevisionProperty)) return false;
    RevisionProperty that = (RevisionProperty) o;
    return !(name != null ? !name.equals(that.name) : that.name != null);
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }

  @Override
  public String toString() {
    return name;
  }

}
