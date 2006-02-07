package de.berlios.sventon.command;

import de.berlios.sventon.diff.DiffException;
import org.tmatesoft.svn.core.io.SVNFileRevision;

import java.util.*;

/**
 * DiffCommand.
 * <p/>
 * Command class used to parse and bundle diffing from/to details.
 *
 * @author jesper@users.berlios.de
 */
public class DiffCommand {

  private final long fromRevision;
  private final long toRevision;
  private final String fromPath;
  private final String toPath;

  /**
   * Constructor.
   * Used when diffing two given entries with given path and revision details.
   *
   * @param parameters The string array containing exactly two entries in the format,
   *                   <i>pathAndFilename;;revision</i>.
   * @throws DiffException            if unable to parse given input.
   * @throws IllegalArgumentException if argument is null or array does not contain
   *                                  exactly two entries.
   */
  public DiffCommand(final String[] parameters) throws DiffException {
    String[] pathAndRevision;

    if (parameters == null || parameters.length != 2) {
      throw new IllegalArgumentException("Parameter list must contain exactly two entries");
    }

    try {
      pathAndRevision = parameters[0].split(";;");
      toPath = pathAndRevision[0];
      toRevision = Long.parseLong(pathAndRevision[1]);
      pathAndRevision = parameters[1].split(";;");
      fromPath = pathAndRevision[0];
      fromRevision = Long.parseLong(pathAndRevision[1]);
    } catch (Throwable ex) {
      throw new DiffException("Unable to diff. Unable to parse revision and path", ex);
    }
  }

  /**
   * Constructor.
   * Used when diffing an entry to its previous entry in history.
   *
   * @param revisions The list containing at least two <code>SVNFileRevision</code> objects.
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
    Iterator revisionIterator = revisions.iterator();
    // Grab the latest..
    SVNFileRevision toRevision = (SVNFileRevision) revisionIterator.next();
    this.toPath = toRevision.getPath();
    this.toRevision = toRevision.getRevision();
    // ..and the previous one...
    SVNFileRevision fromRevision = (SVNFileRevision) revisionIterator.next();
    this.fromPath = fromRevision.getPath();
    this.fromRevision = fromRevision.getRevision();
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
  public long getFromRevision() {
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
  public long getToRevision() {
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
