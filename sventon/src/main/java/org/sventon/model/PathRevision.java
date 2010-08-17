package org.sventon.model;

/**
 * Represents a, according to the repository, valid path/revision combination.
 *
 * @author jesper@sventon.org
 */
public class PathRevision implements Comparable<PathRevision> {

  /**
   * The delimiter between the path and the revision values.
   */
  public static final String DELIMITER = "@";

  private final String path;

  private final Revision revision;

  /**
   * Constructor.
   *
   * @param path     Path to the dir entry.
   * @param revision Revision
   */
  public PathRevision(final String path, final Revision revision) {
    this.path = path;
    this.revision = revision;
  }

  /**
   * @return The path
   */
  public String getPath() {
    return path;
  }

  /**
   * @return The revision
   */
  public Revision getRevision() {
    return revision;
  }

  @Override
  public int compareTo(PathRevision o) {
    return this.getRevision().compareTo(o.getRevision());
  }

  @Override
  public String toString() {
    return path + DELIMITER + revision;
  }

}
