package de.berlios.sventon.ctrl;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.StringUtils;
import org.tmatesoft.svn.core.SVNDirEntry;

/**
 * Represents an entry in the repository.
 * 
 * @author jesper@users.berlios.de
 */
public class RepositoryEntry {

  /** The <code>SVNDirEntry</code>. */
  private SVNDirEntry entry;

  /** Full path to <code>SVNDirEntry</code>. */
  private String entryPath;

  /**
   * Mount point offset. If this is set method
   * {@link #getFullEntryNameStripMountPoint} can be used to get the path to the
   * entry with the {@link #mountPoint} removed.
   */
  private String mountPoint;

  /**
   * Constructor.
   * 
   * @param entry The <code>SVNDirEntry</code>.
   * @param entryPath The entry repository path.
   * @throws IllegalArgumentException if any of the parameters are null.
   */
  public RepositoryEntry(final SVNDirEntry entry, final String entryPath) {
    if (entryPath == null) {
      throw new IllegalArgumentException("entryPath cannot be null.");
    }
    if (entry == null) {
      throw new IllegalArgumentException("entry cannot be null.");
    }
    this.entryPath = entryPath;
    this.entry = entry;
  }

  /**
   * Constructor.
   * 
   * @param entry The <code>SVNDirEntry</code>.
   * @param entryPath The entry repository path.
   * @param mountPoint The mount point in the repository.
   * @throws IllegalArgumentException if any of the parameters are null.
   */
  public RepositoryEntry(final SVNDirEntry entry, final String entryPath, final String mountPoint) {
    if (entryPath == null) {
      throw new IllegalArgumentException("entryPath cannot be null.");
    }
    if (entry == null) {
      throw new IllegalArgumentException("entry cannot be null.");
    }
    this.entryPath = entryPath;
    this.entry = entry;
    this.mountPoint = mountPoint;
  }

  /**
   * Gets the entry.
   * 
   * @return The <code>SVNDirEntry</code>
   */
  public SVNDirEntry getEntry() {
    return entry;
  }

  /**
   * Gets the entry name.
   * 
   * @return The name.
   */
  public String getName() {
    return entry.getName();
  }

  /**
   * Gets the entry path.
   * 
   * @return The full entry path
   */
  public String getEntryPath() {
    return entryPath;
  }

  /**
   * Gets the entry name including full path.
   * 
   * @return The name and full path.
   */
  public String getFullEntryName() {
    return entryPath + getName();
  }

  /**
   * Gets the entry name including full path but with initial mount point
   * removed. If mount point is not set thie method gives the same result at
   * {@link #getFullEntryName()}
   * 
   * @return The name and full path.
   */
  public String getFullEntryNameStripMountPoint() {
      return StringUtils.removeStart(getFullEntryName(), mountPoint);
  }

  /**
   * Gets the full entry name in a display friendly format. <p/> The file name
   * and path will be abbreviated down to 50 characters.
   * 
   * @return The abbreviated display friendly entry name
   */
  public String getFriendlyFullEntryName() {
    return StringUtils.reverse(StringUtils.abbreviate(new StringBuilder(getFullEntryName()).reverse().toString(), 50));
  }

  /**
   * @return String representation of this object.
   */
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
  }

}
