/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.command;

import de.berlios.sventon.diff.DiffException;
import de.berlios.sventon.util.PathUtil;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * DiffCommand.
 * <p/>
 * Command class used to parse and bundle diffing from/to details.
 *
 * @author jesper@users.berlios.de
 */
public final class DiffCommand {

  /**
   * From revision.
   */
  private final SVNRevision fromRevision;

  /**
   * To revision.
   */
  private final SVNRevision toRevision;

  /**
   * From path.
   */
  private final String fromPath;

  /**
   * To path.
   */
  private final String toPath;

  /**
   * Constructor.
   * Used when diffing an entry to its previous entry in history.
   *
   * @param revisions The list containing at least two <code>SVNFileRevision</code> objects.
   *                  The first is assumed to be the <i>to (i.e. latest)</i> revision,
   *                  the second to be the <i>from (i.e. oldest)</i> revision.
   * @throws DiffException if given list does not contain at least two entries.
   */
  public DiffCommand(final List<SVNFileRevision> revisions) throws DiffException {

    if (revisions == null || revisions.size() < 2) {
      throw new DiffException("The entry does not have a history.");
    }

    // Reverse to get latest first
    Collections.sort(revisions, new Comparator<SVNFileRevision>() {
      public int compare(final SVNFileRevision o1, final SVNFileRevision o2) {
        return (o2.getRevision() < o1.getRevision() ? -1 : (o2.getRevision() == o1.getRevision() ? 0 : 1));
      }
    });
    final Iterator revisionIterator = revisions.iterator();
    // Grab the latest..
    final SVNFileRevision toRevision = (SVNFileRevision) revisionIterator.next();
    this.toPath = toRevision.getPath();
    this.toRevision = SVNRevision.create(toRevision.getRevision());
    // ..and the previous one...
    final SVNFileRevision fromRevision = (SVNFileRevision) revisionIterator.next();
    this.fromPath = fromRevision.getPath();
    this.fromRevision = SVNRevision.create(fromRevision.getRevision());
  }

  /**
   * Gets the diff <i>from</i> path.
   *
   * @return The path.
   */
  public String getFromPath() {
    return fromPath;
  }

  /**
   * Gets the from target.
   *
   * @return From target, i.e. file name without path.
   */
  public String getFromTarget() {
    return PathUtil.getTarget(fromPath);
  }

  /**
   * Gets the diff <i>from</i> revision.
   *
   * @return The revision.
   */
  public SVNRevision getFromRevision() {
    return fromRevision;
  }

  /**
   * Gets the diff <i>to</i> path.
   *
   * @return The path.
   */
  public String getToPath() {
    return toPath;
  }

  /**
   * Gets the to target.
   *
   * @return To target, i.e. file name without path.
   */
  public String getToTarget() {
    return PathUtil.getTarget(toPath);
  }

  /**
   * Gets the diff <i>to</i> revision.
   *
   * @return The revision.
   */
  public SVNRevision getToRevision() {
    return toRevision;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}
