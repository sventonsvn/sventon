package de.berlios.sventon.command;

import de.berlios.sventon.diff.DiffException;

/**
 * DiffCommand.
 * <p/>
 * Command class used to parse and bundle request parameters for diffing.
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
      throw new IllegalArgumentException("Parameter list must contain exactly two entries.");
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

}
