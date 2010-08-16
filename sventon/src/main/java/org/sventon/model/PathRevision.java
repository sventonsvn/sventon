package org.sventon.model;

import java.util.HashMap;
import java.util.Map;

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
   * Map holding the RevisionProperties and the corresponding (String) value.
   */
  private Map<RevisionProperty, String> properties = new HashMap<RevisionProperty, String>();

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

  /**
   * Add a RevisionProperty to this PathRevision.
   *
   * @param revisionProperty the RevisionProperty
   * @param revisionPropertyValue the string value for this property
   */
  //TODO: Is it enough with string values or do we need more complex types... 
  public void addProperty(RevisionProperty revisionProperty, String revisionPropertyValue) {
     properties.put(revisionProperty, revisionPropertyValue);
  }

  public String getProperty(RevisionProperty property) {
    return properties.get(property);
  }
}
