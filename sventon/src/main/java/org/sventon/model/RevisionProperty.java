package org.sventon.model;

/**
 *
 */
public enum RevisionProperty {
     /**
     * An <span class="javastring">"svn:author"</span> revision
     * property (that holds the name of the revision's author).
     */
    AUTHOR("svn:author"),
    /**
     * An <span class="javastring">"svn:log"</span> revision property -
     * the one that stores a log message attached to a revision
     * during a commit operation.
     */
    LOG("svn:log"),
    /**
     * An <span class="javastring">"svn:date"</span> revision property
     * that is a date & time stamp representing the time when the
     * revision was created.
     */
    DATE("svn:date");

  private final String name;

  RevisionProperty(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static RevisionProperty byName(String revisionPropertyName) {
    for (RevisionProperty revisionProperty : RevisionProperty.values()) {
      if (revisionProperty.getName().equals(revisionPropertyName)){
        return revisionProperty;
      }
    }

    throw new IllegalArgumentException("No such RevisionProperty: " + revisionPropertyName);
  }
}
