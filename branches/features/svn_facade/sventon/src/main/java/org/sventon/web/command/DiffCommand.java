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
package org.sventon.web.command;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.sventon.diff.DiffException;
import org.sventon.model.DiffStyle;
import org.sventon.model.Revision;
import org.sventon.util.PathUtil;
import org.tmatesoft.svn.core.io.SVNFileRevision;

import java.util.Arrays;
import java.util.Comparator;

/**
 * DiffCommand.
 * <p/>
 * Command class used to parse and bundle diffing from/to details.
 * <p/>
 * A diff can be made between two arbitrary entries or by a single entry and
 * it's previous revision. In the first case, the method
 * {@link #setEntries(org.tmatesoft.svn.core.io.SVNFileRevision[])} setEntries} will be used.
 * A diff with previous revision will use the main path set by calling {@link #setPath(String)} }
 *
 * @author jesper@sventon.org
 */
public final class DiffCommand extends BaseCommand {

  private static final FileRevisionComparator FILE_REVISION_COMPARATOR = new FileRevisionComparator();

  /**
   * From revision.
   */
  private SVNFileRevision fromFileRevision;

  /**
   * To revision.
   */
  private SVNFileRevision toFileRevision;

  /**
   * The requested diff style.
   */
  private DiffStyle style = DiffStyle.unspecified;

  /**
   * Sets the requested diff style.
   *
   * @param style Style
   */
  public void setStyle(final DiffStyle style) {
    Validate.notNull(style);
    this.style = style;
  }

  /**
   * Used when diffing two arbitrary entries.
   *
   * @param entries Array containing two <code>SVNFileRevision</code> objects.
   * @throws IllegalArgumentException       if given list does not contain two entries.
   * @throws org.sventon.diff.DiffException if entry does not have a history.
   */
  public void setEntries(final SVNFileRevision[] entries) throws DiffException {
    Validate.notNull(entries);

    if (entries.length < 2) {
      throw new DiffException("The entry does not have a history.");
    }

    Arrays.sort(entries, FILE_REVISION_COMPARATOR);
    toFileRevision = entries[0];
    fromFileRevision = entries[1];
  }

  /**
   * @return True if entries has been set (using {@link #setEntries(org.tmatesoft.svn.core.io.SVNFileRevision[])}).
   */
  public boolean hasEntries() {
    return toFileRevision != null && fromFileRevision != null;
  }

  /**
   * Gets the requested diff style.
   *
   * @return Style
   */
  public DiffStyle getStyle() {
    return style;
  }

  /**
   * Gets the diff <i>from</i> path.
   *
   * @return The path.
   */
  public String getFromPath() {
    return fromFileRevision != null ? fromFileRevision.getPath() : "";
  }

  /**
   * Gets the from target.
   *
   * @return From target, i.e. file name without path.
   */
  public String getFromTarget() {
    return PathUtil.getTarget(getFromPath());
  }

  /**
   * Gets the diff <i>from</i> revision.
   *
   * @return The revision.
   */
  public Revision getFromRevision() {
    return fromFileRevision != null ? Revision.create(fromFileRevision.getRevision()) : Revision.UNDEFINED;
  }

  /**
   * Gets the diff <i>to</i> path.
   *
   * @return The path.
   */
  public String getToPath() {
    return toFileRevision != null ? toFileRevision.getPath() : "";
  }

  /**
   * Gets the to target.
   *
   * @return To target, i.e. file name without path.
   */
  public String getToTarget() {
    return PathUtil.getTarget(getToPath());
  }

  /**
   * Gets the diff <i>to</i> revision.
   *
   * @return The revision.
   */
  public Revision getToRevision() {
    return toFileRevision != null ? Revision.create(toFileRevision.getRevision()) : Revision.UNDEFINED;
  }

  @Override
  public boolean equals(final Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("DiffCommand {");
    if (hasEntries()) {
      sb.append("from: ");
      sb.append(getFromPath());
      sb.append("@");
      sb.append(getFromRevision());
      sb.append(", to: ");
      sb.append(getToPath());
      sb.append("@");
      sb.append(getToRevision());
    } else {
      sb.append("from: ");
      sb.append(getPath());
      sb.append("@");
      sb.append(getRevision());
      sb.append(", to: ");
      sb.append(getPath());
      sb.append("@");
      sb.append(getRevision());
      sb.append("-1");
    }
    sb.append(", style: ");
    sb.append(getStyle());
    sb.append("}");
    return sb.toString();
  }

  private static class FileRevisionComparator implements Comparator<SVNFileRevision> {
    @Override
    public int compare(SVNFileRevision o1, SVNFileRevision o2) {
      return (o2.getRevision() < o1.getRevision() ? -1 : (o2.getRevision() == o1.getRevision() ? 0 : 1));
    }
  }

}
