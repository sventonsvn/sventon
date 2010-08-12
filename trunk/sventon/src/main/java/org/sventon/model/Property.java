package org.sventon.model;

/**
 *
 */
public enum Property {
  // svn:entry: properties
  CHECKSUM(Property.SVN_ENTRY_PREFIX + "checksum"),
  COMMITTED_DATE(Property.SVN_ENTRY_PREFIX + "committed-date"),
  COMMITTED_REVISION(Property.SVN_ENTRY_PREFIX + "committed-rev"),
  LAST_AUTHOR(Property.SVN_ENTRY_PREFIX + "last_author"),

  REVISION(Property.SVN_ENTRY_PREFIX + "revision"),
  HAS_PROPS(Property.SVN_ENTRY_PREFIX + "has-props"),
  HAS_PROP_MODS(Property.SVN_ENTRY_PREFIX + "has-prop-mods"),
  CACHABLE_PROPS(Property.SVN_ENTRY_PREFIX + "cachable-props"),
  PRESENT_PROPS(Property.SVN_ENTRY_PREFIX + "present-props"),
  KEEP_LOCAL(Property.SVN_ENTRY_PREFIX + "keep-local"),
  CHANGELIST(Property.SVN_ENTRY_PREFIX + "changelist"),
  WORKING_SIZE(Property.SVN_ENTRY_PREFIX + "working-size"),
  DEPTH(Property.SVN_ENTRY_PREFIX + "depth"),
  FILE_EXTERNAL_PATH(Property.SVN_ENTRY_PREFIX + "file-external-path"),
  FILE_EXTERNAL_REVISION(Property.SVN_ENTRY_PREFIX + "file-external-revision"),
  FILE_EXTERNAL_PEG_REVISION(Property.SVN_ENTRY_PREFIX + "file-external-peg-revision"),
  TREE_CONFLICT_DATA(Property.SVN_ENTRY_PREFIX + "tree-conflicts"),
  URL(Property.SVN_ENTRY_PREFIX + "url"),
  COPYFROM_URL(Property.SVN_ENTRY_PREFIX + "copyfrom-url"),
  COPYFROM_REVISION(Property.SVN_ENTRY_PREFIX+ "copyfrom-rev"),
  SCHEDULE(Property.SVN_ENTRY_PREFIX + "schedule"),
  COPIED(Property.SVN_ENTRY_PREFIX + "copied"),
  UUID(Property.SVN_ENTRY_PREFIX + "uuid"),
  REPOS(Property.SVN_ENTRY_PREFIX + "repos"),
  PROP_TIME(Property.SVN_ENTRY_PREFIX + "prop-time"),
  
  EXT_TIME(Property.SVN_ENTRY_PREFIX + "text-time"),
  NAME(Property.SVN_ENTRY_PREFIX + "name"),
  KIND(Property.SVN_ENTRY_PREFIX + "kind"),
  CONFLICT_OLD(Property.SVN_ENTRY_PREFIX + "conflict-old"),
  CONFLICT_NEW(Property.SVN_ENTRY_PREFIX + "conflict-new"),
  CONFLICT_WRK(Property.SVN_ENTRY_PREFIX + "conflict-wrk"),
  PROP_REJECT_FILE(Property.SVN_ENTRY_PREFIX + "prop-reject-file"),
  DELETED(Property.SVN_ENTRY_PREFIX + "deleted"),
  ABSENT(Property.SVN_ENTRY_PREFIX + "absent"),
  INCOMPLETE(Property.SVN_ENTRY_PREFIX + "incomplete"),
  CORRUPTED(Property.SVN_ENTRY_PREFIX + "corrupted"),
  LOCK_TOKEN(Property.SVN_ENTRY_PREFIX + "lock-token"),
  LOCK_COMMENT(Property.SVN_ENTRY_PREFIX + "lock-comment"),
  LOCK_OWNER(Property.SVN_ENTRY_PREFIX + "lock-owner"),
  LOCK_CREATION_DATE(Property.SVN_ENTRY_PREFIX + "lock-creation-date"),
  NEEDS_LOCK(Property.SVN_PREFIX + "needs-lock"),


  //svn: properties
  KEYWORDS(Property.SVN_PREFIX + "keywords"),
  MIME_TYPE(Property.SVN_PREFIX + "mime-type"),
  EOL_STYLE(Property.SVN_PREFIX + "eol-style"),
  IGNORE(Property.SVN_PREFIX + "ignore"),
  EXECUTABLE(Property.SVN_PREFIX + "executable"),
  EXTERNALS(Property.SVN_PREFIX + "externals"),
  SPECIAL(Property.SVN_PREFIX + "special"),
  MERGE_INFO(Property.SVN_PREFIX + "mergeinfo"),

  ;

  private final String name;

  public static final String SVN_PREFIX ="svn:";
  public static final String SVN_ENTRY_PREFIX ="svn:entry:";

  Property(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  /**
   * Check to see if the given string can be considered as describing a text MIME type
   *
   * @param mimeType the MIME type
   * @return true if given string describes a MIME type, otherwise false.
   */
  public static boolean isTextMimeType(String mimeType) {
        return  (mimeType == null || mimeType.startsWith("text/"));
  }

  /**
   * Lookup the Property from name string.
   *
   * @param name the name of the property including any svn prefix (such as "svn:" or "svn:entry") if applicable
   * @return the Property corresponding to the given name. Throws IllegalArgumentException if the property does not exist.
   */
  public static Property fromName(final String name){
    for (Property property : Property.values()) {
      if (property.getName().equals(name)){
        return property;
      }
    }

    throw new IllegalArgumentException("No such svn Property: " + name);
  }
}
