/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.model;

import org.apache.commons.lang.math.NumberUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a revision in Subversion.
 */
public final class Revision implements Serializable, Comparable<Revision> {

  private static final long serialVersionUID = 2663123883312721991L;

  public static final Revision FIRST = Revision.create(1);

  public static final Revision UNDEFINED = new Revision(-1, null, false);

  public static final Revision HEAD = new Revision(-1, null, true);

  private final boolean headRevision;

  private final long revisionNumber;

  private final Date revisionDate;

  /**
   * Private C-tor if immutable Revision value object.
   *
   * @param revisionNumber Number
   * @param revisionDate   Revision
   * @param headRevision   Set to true if the revision number is the latest revision in the repository.
   */
  private Revision(final long revisionNumber, final Date revisionDate, final boolean headRevision) {
    this.revisionNumber = revisionNumber;
    this.revisionDate = revisionDate;
    this.headRevision = headRevision;
  }

  /**
   * Constructor.
   *
   * @param revision Revision number
   * @return Revision
   */
  public static Revision create(final long revision) {
    return revision < 0 ? UNDEFINED : new Revision(revision, null, false);
  }

  /**
   * Constructor.
   *
   * @param revision Revision number
   * @return Revision
   */
  public static Revision createHeadRevision(final long revision) {
    return new Revision(revision, null, true);
  }

  /**
   * Constructor.
   *
   * @param revision Revision date
   * @return Revision
   */
  public static Revision create(final Date revision) {
    return new Revision(UNDEFINED.getNumber(), revision, false);
  }

  /**
   * Constructor.
   *
   * @param revision       Revision date
   * @param revisionNumber Revision number corresponding to the 'revision' parameter.
   * @return Revision
   */
  public static Revision create(final Date revision, final long revisionNumber) {
    return new Revision(revisionNumber, revision, false);
  }

  /**
   * Parse a given revision text into a Revision. The revision could be a number, date or a NamedRevision
   *
   * @param text Revision text to parse.
   * @return Created revision.
   */
  public static Revision parse(final String text) {
    if (text == null) {
      return UNDEFINED;
    }

    final String rev = text.trim();

    if (isDateRevision(rev)) {
      try {
        final Date date = DateUtil.parseISO8601(trimDateBrackets(rev));
        return create(date);
      } catch (IllegalArgumentException e) {
        return Revision.UNDEFINED;
      }
    } else if (isNumberRevision(rev)) {
      return create(Long.parseLong(rev));
    } else if (isNamedRevision(rev)) {
      return HEAD;
    } else {
      return UNDEFINED;
    }
  }

  private static boolean isNamedRevision(String rev) {
    return rev != null && "HEAD".equalsIgnoreCase(rev);
  }

  private static boolean isNumberRevision(String rev) {
    return rev != null && !rev.equals("-1") && NumberUtils.isNumber(rev);
  }

  private static boolean isDateRevision(String rev) {
    return rev != null && rev.startsWith("{") && rev.endsWith("}");
  }

  private static String trimDateBrackets(final String text) {
    return text.substring(1, text.length() - 1);
  }

  /**
   * @return The revision as a number.
   */
  public long getNumber() {
    return revisionNumber;
  }

  /**
   * @return The time stamp used to specify the revision.
   */
  public Date getDate() {
    return revisionDate;
  }

  @Override
  public int compareTo(Revision o) {
    return o.getNumber() < getNumber() ? -1 : (o.getNumber() == getNumber() ? 0 : 1);
  }

  public boolean isHeadRevision() {
    return headRevision;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Revision)) return false;
    Revision revision = (Revision) o;
    if (headRevision != revision.headRevision) return false;
    if (revisionNumber != revision.revisionNumber) return false;
    if (revisionDate != null ? !revisionDate.equals(revision.revisionDate) : revision.revisionDate != null)
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    int result = (int) (revisionNumber ^ (revisionNumber >>> 32));
    result = 31 * result + (revisionDate != null ? revisionDate.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return headRevision ? "HEAD" : revisionDate != null ? "{" + DateUtil.formatISO8601(revisionDate) + "}" : Long.toString(revisionNumber);
  }
}
