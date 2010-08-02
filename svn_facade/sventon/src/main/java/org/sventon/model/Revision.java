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

import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a revision in Subversion.
 */
public class Revision implements Serializable {

  private static final long serialVersionUID = 2663123883312721991L;

  public static final Revision UNDEFINED = new Revision(SVNRevision.UNDEFINED);

  public static final Revision HEAD = new Revision(SVNRevision.HEAD);

  private static final Map<String, Revision> NAMED_REVISIONS = new HashMap<String, Revision>();

  public static final long FIRST = 1;

  private final SVNRevision revision;

  static {
    NAMED_REVISIONS.put(UNDEFINED.toString(), UNDEFINED);
    NAMED_REVISIONS.put(HEAD.toString(), HEAD);
  }

  /**
   * Constructor.
   *
   * @param revision Revision
   */
  private Revision(final SVNRevision revision) {
    this.revision = revision;
  }

  /**
   * Constructor.
   *
   * @param revision Revision
   * @return Revision
   */
  public static Revision create(final long revision) {
    return parse(String.valueOf(revision));
  }

  /**
   * @return The revision as a number.
   */
  public long getNumber() {
    return revision.getNumber();
  }

  /**
   * @param text Revision text to parse.
   * @return Created revision.
   */
  public static Revision parse(String text) {
    final SVNRevision svnRevision = SVNRevision.parse(text);
    final Revision namedRevision = NAMED_REVISIONS.get(svnRevision.getName());
    if (namedRevision != null) {
      return namedRevision;
    }
    return new Revision(svnRevision);
  }

  /**
   * @return The time stamp used to specify the revision.
   */
  public Date getDate() {
    return revision.getDate();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final Revision revision1 = (Revision) o;
    return !(revision != null ? !revision.equals(revision1.revision) : revision1.revision != null);
  }

  @Override
  public int hashCode() {
    return revision != null ? revision.hashCode() : 0;
  }

  @Override
  public String toString() {
    return revision.toString();
  }
}
