package de.berlios.sventon.svnsupport;

import org.tmatesoft.svn.core.io.SVNDirEntry;
import org.apache.commons.lang.StringUtils;

/**
 * Represents an entry in the revision index.
 * @see de.berlios.sventon.svnsupport.RevisionIndex
 * @author jesper@users.berlios.de
 */
public class IndexEntry {

  /** The <code>SVNDirEntry</code>. */
  private SVNDirEntry entry;

  /** Full path to <code>SVNDirEntry</code>. */
  private String entryPath;

  /**
   * Constructor.
   * @param entry The <code>SVNDirEntry</code>.
   * @param entryPath The full path to the entry.
   * @throws IllegalArgumentException if any of the parameters are null.
   */
  public IndexEntry(final SVNDirEntry entry, final String entryPath) {
    if (entry == null || entryPath == null) {
      throw new IllegalArgumentException("entry or entryPath cannot be null.");
    }
    this.entry = entry;
    this.entryPath = entryPath;
  }

  /**
   * Gets the index entry.
   * @see org.tmatesoft.svn.core.io.SVNDirEntry
   * @return The <code>SVNDirEntry</code>
   */
  public SVNDirEntry getEntry() {
    return entry;
  }

  /**
   * Gets the entry path.
   * @return The full entry path
   */
  public String getEntryPath() {
    return entryPath;
  }

  /**
   * Gets the entry name.
   * @return The name.
   */
  public String getName() {
    return entry.getName();
  }

  /**
   * Gets the entry name including full path.
   * @return The name and full path.
   */
  public String getFullEntryName() {
    return entryPath + getName();
  }

  /**
   * Gets the full entry name in a display friendly format.
   * <p/>
   * The file name and path will be abbreviated down to 50 characters.
   * @return The abbreviated display friendly entry name
   */
  public String getFriendlyFullEntryName() {
    return StringUtils.reverse(StringUtils.abbreviate(new StringBuilder(getFullEntryName()).reverse().toString(), 50));
  }

}
