package de.berlios.sventon.ctrl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.tmatesoft.svn.core.SVNDirEntry;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents an entry in the repository.
 *
 * @author jesper@users.berlios.de
 */
public class RepositoryEntry implements Serializable {

  private static final long serialVersionUID = 3617229449081593805L;

  /**
   * Full path to <code>SVNDirEntry</code>.
   */
  private String entryPath;
  private String entryName;
  private String entryKind;
  private long entrySize;
  private boolean entryHasProperties;
  private long entryFirstRevision;
  private Date entryCreatedDate;
  private String entryLastAuthor;
  private String entryCommitMessage;

  /**
   * Mount point offset. If this is set method
   * {@link #getFullEntryNameStripMountPoint} can be used to get the path to the
   * entry with the {@link #mountPoint} removed.
   */
  private String mountPoint;

  /**
   * Constructor.
   *
   * @param entry      The <code>SVNDirEntry</code>.
   * @param entryPath  The entry repository path.
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
    this.mountPoint = mountPoint;
    copyEntry(entry);
  }

  private void copyEntry(final SVNDirEntry entry) {
    this.entryLastAuthor = entry.getAuthor();
    this.entryCommitMessage = entry.getCommitMessage();
    this.entryCreatedDate = entry.getDate();
    this.entryKind = entry.getKind().toString();
    this.entryName = entry.getName();
    this.entryFirstRevision = entry.getRevision();
    this.entrySize = entry.size();
    this.entryHasProperties = entry.hasProperties();
  }

  /**
   * Gets the entry name.
   *
   * @return The name.
   */
  public String getName() {
    return entryName;
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
   * Gets the path.
   *
   * @return the path for this entry
   */
  public String getPath() {
    return entryPath;
  }

  /**
   * Retrieves the entry syze in bytes.
   *
   * @return the size of this entry in bytes
   */
  public long getSize() {
    return entrySize;
  }

  /**
   * Tells if the entry has any properties.
   *
   * @return <code>true</code> if has, <code>false</code> - otherwise
   */
  public boolean hasProperties() {
    return entryHasProperties;
  }

  /**
   * Retrieves the entry node kind - whether it's a directory or file, for instance.
   *
   * @return the node kind of this entry. Can be <code>none</code>, <code>unknown</code>,
   *         <code>file</code> or <code>dir</code>.
   */
  public String getKind() {
    return entryKind;
  }

  /**
   * Returns the date the entry was created at.
   *
   * @return the creation date
   */
  public Date getDate() {
    return entryCreatedDate;
  }

  /**
   * Gets the revision
   * at which the entry was last modified in the repository.
   *
   * @return the revision of this entry when it was last changed
   */
  public long getRevision() {
    return entryFirstRevision;
  }

  /**
   * Retrieves the name of the person who was the last to update
   * this entry in the repository.
   *
   * @return the last author's name.
   */
  public String getAuthor() {
    return entryLastAuthor;
  }

  /**
   * Retrieves the commit message.
   *
   * @return the commit message.
   */
  public String getCommitMessage() {
    return entryCommitMessage;
  }

  /**
   * @return String representation of this object.
   */
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
  }

}
