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

import org.apache.commons.lang.math.NumberUtils;
import org.sventon.util.DateUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a revision in Subversion.
 */
public class Revision implements Serializable, Comparable<Revision> {

  private static final long serialVersionUID = 2663123883312721991L;
  public static final Revision UNDEFINED = create(NamedRevision.UNDEFINED);
  public static final Revision HEAD = create(NamedRevision.HEAD);

  public enum NamedRevision{
    UNDEFINED("UNDEFINED"),
    HEAD("HEAD");
    
    private final String name;

    NamedRevision(final String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public static boolean isValidRevisionName(final String name) {
      for (NamedRevision namedRevision : values()) {
        if (name.equalsIgnoreCase(namedRevision.getName())){
          return true;
        }
      }
      return false;
    }

    public static NamedRevision byNameIgnoreCase(final String name) {
      for (NamedRevision namedRevision : values()) {
        if (name.equalsIgnoreCase(namedRevision.getName())){
          return namedRevision;
        }
      }
      throw new IllegalArgumentException("No such NamedRevision: " + name);
    }
  }

  public static final long FIRST = 1;
  public static final long UNDEFINED_NUMBER = -1;


  private final long revisionNumber;
  private final Date revisionDate;
  private final NamedRevision namedRevision;

  /**
   * Private C-tor if immutable Revision value object.
   *
   */
  private Revision(final long revisionNumber, final Date revisionDate, final NamedRevision namedRevision) {
    this.revisionNumber = revisionNumber;
    this.revisionDate = revisionDate;
    this.namedRevision = namedRevision;
  }

  /**
   * Constructor.
   *
   * @param revision Revision number
   * @return Revision
   */
  public static Revision create(final long revision) {
    return new Revision(revision, null, NamedRevision.UNDEFINED);
  }


  /**
   * Constructor.
   *
   * @param revision Revision date
   * @return Revision
   */
  public static Revision create(final Date revision) {
    return new Revision(UNDEFINED_NUMBER, revision, NamedRevision.UNDEFINED);
  }


  /**
   * Constructor.
   *
   * @param revision Revision the named revision (see {@link NamedRevision})
   * @return Revision
   */
  public static Revision create(final NamedRevision revision) {
    return new Revision(UNDEFINED_NUMBER, null, revision);
  }



  /**
   * Parse a given revision text into a Revision. The revision could be a number, date or a NamedRevision
   *
   * @param text Revision text to parse.
   * @return Created revision.
   */
  public static Revision parse(final String text) {
    if (text == null){
      return UNDEFINED;
    }

    String rev = trimText(text);

    if (isDateRevision(rev)){
      return create(DateUtil.parseISO8601(trimDateBrackets(rev)));
    } else if (isNumberRevision(rev)) {
      return create(Long.parseLong(rev));
    } else if (isNamedRevision(rev)) {
      return create(NamedRevision.byNameIgnoreCase(rev));
    } else {
      return UNDEFINED;
    }
  }

  private static boolean isNamedRevision(String rev) {
    return rev != null && NamedRevision.isValidRevisionName(rev);
  }

  private static boolean isNumberRevision(String rev) {
    return rev != null && !rev.equals("-1") && NumberUtils.isNumber(rev);
  }

  private static boolean isDateRevision(String rev) {
    return rev != null && rev.startsWith("{") && rev.endsWith("}");
  }

  private static String trimText(final String text) {
    if (text.startsWith("-r")){
      return text.substring("-r".length()).trim();
    }

    return text.trim();
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

  /**
   * @return the NamedRevision if applicable or NamedRevision.UNDEFINED if not.
   */
  public NamedRevision getNamedRevision() {
    return namedRevision;
  }


  public boolean isNumberRevision() {
    return revisionNumber != UNDEFINED_NUMBER && revisionDate == null && namedRevision.equals(NamedRevision.UNDEFINED);
  }

  public boolean isDateRevision() {
    return revisionNumber == UNDEFINED_NUMBER && revisionDate != null && namedRevision.equals(NamedRevision.UNDEFINED);
  }

  public boolean isNamedRevision() {
    return revisionNumber == UNDEFINED_NUMBER && revisionDate == null && namedRevision != null;
  }


  @Override
  public int compareTo(Revision o) {
    return o.getNumber() < getNumber() ? -1 : (o.getNumber() == getNumber() ? 0 : 1);
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Revision revision = (Revision) o;

    if (revisionNumber != revision.revisionNumber) return false;
    if (namedRevision != revision.namedRevision) return false;
    if (revisionDate != null ? !revisionDate.equals(revision.revisionDate) : revision.revisionDate != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = (int) (revisionNumber ^ (revisionNumber >>> 32));
    result = 31 * result + (revisionDate != null ? revisionDate.hashCode() : 0);
    result = 31 * result + (namedRevision != null ? namedRevision.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return isNumberRevision() ? Long.toString(revisionNumber) :
         isDateRevision() ? DateUtil.formatISO8601(revisionDate) :
         isNamedRevision() ? namedRevision.getName() :
         "Invalid Revision";
  }
}
