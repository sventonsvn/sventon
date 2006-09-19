/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.command;

import de.berlios.sventon.diff.DiffException;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.*;

/**
 * DiffCommand.
 * <p/>
 * Command class used to parse and bundle diffing from/to details.
 *
 * @author jesper@users.berlios.de
 */
public class DiffCommand {

  private final SVNRevision fromRevision;
  private final SVNRevision toRevision;
  private final String fromPath;
  private final String toPath;

  /**
   * Constructor.
   * Used when diffing two given entries with given path and revision details.
   *
   * @param parameters The string array containing exactly two entries in the format,
   *                   <i>pathAndFilename;;revision</i>.
   *                   The first is assumed to be the <i>to (i.e. latest)</i> revision,
   *                   the second to be the <i>from (i.e. oldest)</i> revision.
   * @throws DiffException            if unable to parse given input.
   * @throws IllegalArgumentException if argument is null or array does not contain
   *                                  exactly two entries.
   */
  public DiffCommand(final String[] parameters) throws DiffException {
    final String[] toPathAndRevision;
    final String[] fromPathAndRevision;

    if (parameters == null || parameters.length != 2) {
      throw new IllegalArgumentException("Parameter list must contain exactly two entries");
    }

    try {
      toPathAndRevision = parameters[0].split(";;");
      toPath = toPathAndRevision[0];

      fromPathAndRevision = parameters[1].split(";;");
      fromPath = fromPathAndRevision[0];

      if (toPathAndRevision.length == 1 && fromPathAndRevision.length == 1) {
        // Assume HEAD revision
        toRevision = SVNRevision.HEAD;
        fromRevision = SVNRevision.HEAD;
      } else {
        toRevision = SVNRevision.parse(toPathAndRevision[1]);
        fromRevision = SVNRevision.parse(fromPathAndRevision[1]);
      }
    } catch (Exception ex) {
      throw new DiffException("Unable to diff. Unable to parse revision and path.", ex);
    }
  }

  /**
   * Constructor.
   * Used when diffing an entry to its previous entry in history.
   *
   * @param revisions The list containing at least two <code>SVNFileRevision</code> objects.
   *                  The first is assumed to be the <i>to (i.e. latest)</i> revision,
   *                  the second to be the <i>from (i.e. oldest)</i> revision.
   * @throws DiffException            if given list does not contain at least two entries.
   * @throws IllegalArgumentException if argument is null or array does not contain
   *                                  exactly two entries.
   */
  public DiffCommand(final List<SVNFileRevision> revisions) throws DiffException {

    if (revisions == null || revisions.size() < 2) {
      throw new DiffException("The entry does not have a history.");
    }

    // Reverse to get latest first
    Collections.reverse(revisions);
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
  public String toString() {
    return "DiffCommand{" +
        "fromRevision=" + fromRevision +
        ", toRevision=" + toRevision +
        ", fromPath='" + fromPath + '\'' +
        ", toPath='" + toPath + '\'' +
        '}';
  }
}
