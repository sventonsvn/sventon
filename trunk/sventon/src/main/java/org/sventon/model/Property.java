package org.sventon.model;

/**
 *
 */
public class Property {
  private final String name;

  private static final String SVN_PREFIX = "svn:";
  private static final String SVN_ENTRY_PREFIX = "svn:entry:";

  public static final Property REVISION = new Property(SVN_ENTRY_PREFIX + "revision");
  public static final Property HAS_PROPS = new Property(SVN_ENTRY_PREFIX + "has-props");
  public static final Property HAS_PROP_MODS = new Property(SVN_ENTRY_PREFIX + "has-prop-mods");
  public static final Property CACHABLE_PROPS = new Property(SVN_ENTRY_PREFIX + "cachable-props");
  public static final Property PRESENT_PROPS = new Property(SVN_ENTRY_PREFIX + "present-props");
  public static final Property KEEP_LOCAL = new Property(SVN_ENTRY_PREFIX + "keep-local");
  public static final Property CHANGELIST = new Property(SVN_ENTRY_PREFIX + "changelist");
  public static final Property CHECKSUM = new Property(SVN_ENTRY_PREFIX + "checksum");
  public static final Property WORKING_SIZE = new Property(SVN_ENTRY_PREFIX + "working-size");
  public static final Property DEPTH = new Property(SVN_ENTRY_PREFIX + "depth");
  public static final Property FILE_EXTERNAL_PATH = new Property(SVN_ENTRY_PREFIX + "file-external-path");
  public static final Property FILE_EXTERNAL_REVISION = new Property(SVN_ENTRY_PREFIX + "file-external-revision");
  public static final Property FILE_EXTERNAL_PEG_REVISION = new Property(SVN_ENTRY_PREFIX + "file-external-peg-revision");
  public static final Property TREE_CONFLICT_DATA = new Property(SVN_ENTRY_PREFIX + "tree-conflicts");
  public static final Property URL = new Property(SVN_ENTRY_PREFIX + "url");
  public static final Property COPYFROM_URL = new Property(SVN_ENTRY_PREFIX + "copyfrom-url");
  public static final Property COPYFROM_REVISION = new Property(SVN_ENTRY_PREFIX+ "copyfrom-rev");
  public static final Property SCHEDULE = new Property(SVN_ENTRY_PREFIX + "schedule");
  public static final Property COPIED = new Property(SVN_ENTRY_PREFIX + "copied");
  public static final Property UUID = new Property(SVN_ENTRY_PREFIX + "uuid");
  public static final Property REPOS = new Property(SVN_ENTRY_PREFIX + "repos");
  public static final Property PROP_TIME = new Property(SVN_ENTRY_PREFIX + "prop-time");
  public static final Property EXT_TIME = new Property(SVN_ENTRY_PREFIX + "text-time");
  public static final Property NAME = new Property(SVN_ENTRY_PREFIX + "name");
  public static final Property KIND = new Property(SVN_ENTRY_PREFIX + "kind");
  public static final Property CONFLICT_OLD = new Property(SVN_ENTRY_PREFIX + "conflict-old");
  public static final Property CONFLICT_NEW = new Property(SVN_ENTRY_PREFIX + "conflict-new");
  public static final Property CONFLICT_WRK = new Property(SVN_ENTRY_PREFIX + "conflict-wrk");
  public static final Property PROP_REJECT_FILE = new Property(SVN_ENTRY_PREFIX + "prop-reject-file");
  public static final Property DELETED = new Property(SVN_ENTRY_PREFIX + "deleted");
  public static final Property ABSENT = new Property(SVN_ENTRY_PREFIX + "absent");
  public static final Property INCOMPLETE = new Property(SVN_ENTRY_PREFIX + "incomplete");
  public static final Property CORRUPTED = new Property(SVN_ENTRY_PREFIX + "corrupted");
  public static final Property LOCK_TOKEN = new Property(SVN_ENTRY_PREFIX + "lock-token");
  public static final Property LOCK_COMMENT = new Property(SVN_ENTRY_PREFIX + "lock-comment");
  public static final Property LOCK_OWNER = new Property(SVN_ENTRY_PREFIX + "lock-owner");
  public static final Property LOCK_CREATION_DATE = new Property(SVN_ENTRY_PREFIX + "lock-creation-date");
  public static final Property LAST_AUTHOR = new Property(SVN_ENTRY_PREFIX + "last-author");
  public static final Property COMMITTED_DATE = new Property(SVN_ENTRY_PREFIX + "committed-date");
  public static final Property COMMITTED_REVISION = new Property(SVN_ENTRY_PREFIX + "committed-rev");

  public static final Property KEYWORDS = new Property(SVN_PREFIX + "keywords");
  public static final Property NEEDS_LOCK = new Property(SVN_PREFIX + "needs-lock");
  public static final Property MIME_TYPE = new Property(SVN_PREFIX + "mime-type");
  public static final Property EOL_STYLE = new Property(SVN_PREFIX + "eol-style");
  public static final Property IGNORE = new Property(SVN_PREFIX + "ignore");
  public static final Property EXECUTABLE = new Property(SVN_PREFIX + "executable");
  public static final Property EXTERNALS = new Property(SVN_PREFIX + "externals");
  public static final Property SPECIAL = new Property(SVN_PREFIX + "special");
  public static final Property MERGE_INFO = new Property(SVN_PREFIX + "mergeinfo");


  public Property(String name) {
    this.name = name;
  }


  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Property property = (Property) o;

    if (name != null ? !name.equals(property.name) : property.name != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }

  //TODO: Move functionality out of this class. Mime-type text and svn property has nothing to do with each other.
  /**
   * Check to see if the given string can be considered as describing a text MIME type
   *
   * @param mimeType the MIME type
   * @return true if given string describes a MIME type, otherwise false.
   */
  public static boolean isTextMimeType(String mimeType) {
    return  (mimeType == null || mimeType.startsWith("text/"));
  }


}
