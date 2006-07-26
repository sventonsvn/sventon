/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.repository;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNLock;

import java.io.Serializable;
import java.util.*;

/**
 * Represents an entry in the repository.
 *
 * @author jesper@users.berlios.de
 */
public class RepositoryEntry implements Serializable {

  public static final int FULL_ENTRY_NAME_MAX_LENGTH = 60;
  private static final long serialVersionUID = 3617229449081593805L;
  private transient SVNLock lock;
  private String entryPath;
  private String entryName;
  private Kind entryKind;
  private long entrySize;
  private boolean entryHasProperties;
  private long entryFirstRevision;
  private Date entryCreatedDate;
  private String entryLastAuthor;
  private String entryLogMessage;
  private String url;

  public enum Kind {
    dir, file, none, unknown, any
  }

  /**
   * Constructor.
   *
   * @param entry     The <code>SVNDirEntry</code>.
   * @param entryPath The entry repository path.
   * @param lock      The lock, null if n/a.
   * @throws IllegalArgumentException if any of the parameters are null.
   */
  public RepositoryEntry(final SVNDirEntry entry, final String entryPath, final SVNLock lock) {

    if (entryPath == null) {
      throw new IllegalArgumentException("entryPath cannot be null.");
    }

    if (entry == null) {
      throw new IllegalArgumentException("entry cannot be null.");
    }

    this.entryPath = entryPath;
    this.lock = lock;
    copyEntry(entry);
  }

  /**
   * Creates a collection of <code>RepositoryEntry</code> objects based
   * on given collection of <code>SVNDirEntry</code> instances.
   *
   * @param entries  Collection of entries.
   * @param basePath Base repository path for the entries.
   * @param locks    Map of active locks. Provide an empty Map to ignore locking details.
   * @return The collection of entries.
   */
  public static List<RepositoryEntry> createEntryCollection(final Collection entries,
                                                            final String basePath,
                                                            final Map<String, SVNLock> locks) {

    final List<RepositoryEntry> dir = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);
    for (final Object ent : entries) {
      final SVNDirEntry entry = (SVNDirEntry) ent;
      dir.add(new RepositoryEntry(entry, basePath, locks.get(basePath + entry.getName())));
    }
    return dir;
  }

  private void copyEntry(final SVNDirEntry entry) {
    this.entryLastAuthor = entry.getAuthor();
    this.entryLogMessage = entry.getCommitMessage();
    this.entryCreatedDate = entry.getDate();
    this.entryKind = Kind.valueOf(entry.getKind().toString());
    this.entryName = entry.getName();
    this.entryFirstRevision = entry.getRevision();
    this.entrySize = entry.getSize();
    this.entryHasProperties = entry.hasProperties();
    this.url = entry.getURL() == null ? null : entry.getURL().toString();
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
   * Gets the entry url.
   *
   * @return The entry url
   */
  public String getUrl() {
    return url;
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
   * Gets the full entry name in a display friendly format. <p/> The file name
   * and path will be abbreviated down to 60 characters.
   *
   * @return The abbreviated display friendly entry name
   * @see #FULL_ENTRY_NAME_MAX_LENGTH
   */
  public String getFriendlyFullEntryName() {
    return StringUtils.reverse(StringUtils.abbreviate(new StringBuilder(getFullEntryName()).reverse().toString(), FULL_ENTRY_NAME_MAX_LENGTH));
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
  public Kind getKind() {
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
   * Retrieves the log message.
   *
   * @return the log message.
   */
  public String getCommitMessage() {
    return entryLogMessage;
  }

  /**
   * Gets the lock for the entry.
   *
   * @return The lock, null if entry hasn't got any.
   */
  public SVNLock getLock() {
    return lock;
  }

  /**
   * @return String representation of this object.
   */
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
  }

}
