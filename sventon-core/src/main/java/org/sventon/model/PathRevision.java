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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a, according to the repository, valid path/revision combination.
 *
 * @author jesper@sventon.org
 */
public class PathRevision implements Serializable, Comparable<PathRevision> {

  private static final long serialVersionUID = 9203048530692746853L;

  /**
   * The delimiter between the path and the revision values.
   */
  public static final String DELIMITER = "@";

  /**
   * Entry path.
   */
  private final String path;

  /**
   * Revision.
   */
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

  public static PathRevision parse(final String entry) {
    return parse(new String[]{entry})[0];
  }

  public static PathRevision[] parse(final String... entries) {
    final List<PathRevision> fileEntries = new ArrayList<PathRevision>();
    for (String entry : entries) {
      if (!entry.contains(DELIMITER)) {
        throw new IllegalArgumentException("Illegal parameter. No delimiter in entry: " + entry);
      }
      final String path = entry.substring(0, entry.lastIndexOf(DELIMITER));
      final String revision = entry.substring(entry.lastIndexOf(DELIMITER) + 1);
      fileEntries.add(new PathRevision(path, Revision.parse(revision)));
    }
    return fileEntries.toArray(new PathRevision[fileEntries.size()]);
  }

  @Override
  public String toString() {
    return path + DELIMITER + revision;
  }

}
