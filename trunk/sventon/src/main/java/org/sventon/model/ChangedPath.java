package org.sventon.model;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 */
public class ChangedPath implements Serializable, Comparable<ChangedPath> {
  private final String path;
  private final String copyPath;
  private final long copyRevision;
  private final ChangeType type;

  public ChangedPath(String path, String copyPath, long copyRevision, ChangeType type) {
    this.path = path;
    this.copyPath = copyPath;
    this.copyRevision = copyRevision;
    this.type = type;
  }

  public ChangeType getType() {
    return type;
  }

  public String getPath() {
    return path;
  }

  public String getCopyPath() {
    return copyPath;
  }

  public long getCopyRevision() {
    return copyRevision;
  }

  @Override
  public int compareTo(ChangedPath that) {
    return getPath().compareTo(that.getPath());
  }
}
