/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.repository;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.tmatesoft.svn.core.SVNDirEntry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Represents an entry in the repository.
 *
 * @author jesper@users.berlios.de
 */
public final class RepositoryEntry implements Serializable {

  public static final int FULL_ENTRY_NAME_MAX_LENGTH = 70;
  private static final long serialVersionUID = 3617229449081593805L;
  private String entryPath;
  private String entryName;
  private Kind entryKind;
  private long entrySize;
  private boolean entryHasProperties;
  private long entryFirstRevision;
  private Date entryCreatedDate;
  private String entryLastAuthor;
  private String entryLogMessage;

  public enum Kind {
    dir, file, none, unknown, any
  }

  /**
   * Constructor.
   *
   * @param entry     The <code>SVNDirEntry</code>.
   * @param entryPath The entry repository path.
   */
  public RepositoryEntry(final SVNDirEntry entry, final String entryPath) {

    if (entryPath == null) {
      throw new IllegalArgumentException("entryPath cannot be null.");
    }

    if (entry == null) {
      throw new IllegalArgumentException("entry cannot be null.");
    }

    this.entryPath = entryPath.intern();
    copyEntry(entry);
  }

  /**
   * Creates a collection of <code>RepositoryEntry</code> objects based
   * on given collection of <code>SVNDirEntry</code> instances.
   *
   * @param entries  Collection of SVNDirEntry.
   * @param basePath Base repository path for the entries.
   * @return The collection of entries.
   */
  public static List<RepositoryEntry> createEntryCollection(final Collection<SVNDirEntry> entries,
                                                            final String basePath) {

    final List<RepositoryEntry> dir = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);
    for (final SVNDirEntry entry : entries) {
      dir.add(new RepositoryEntry(entry, basePath));
    }
    return dir;
  }

  private void copyEntry(final SVNDirEntry entry) {
    this.entryLastAuthor = entry.getAuthor() == null ? null : entry.getAuthor().intern();
    this.entryLogMessage = entry.getCommitMessage();
    this.entryCreatedDate = entry.getDate();
    this.entryKind = Kind.valueOf(entry.getKind().toString());
    this.entryName = entry.getName().intern();
    this.entryFirstRevision = entry.getRevision();
    this.entrySize = entry.getSize();
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
   * Gets the entry url.
   *
   * @return The entry url
   * @deprecated Throws <tt>UnsupportedOperationException</tt> since sventon 1.2 RC3.
   */
  public String getUrl() {
    throw new UnsupportedOperationException();
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
   * @return the creation date, or <tt>null</tt> if no date exists.
   */
  public Date getDate() {
    if (entryCreatedDate != null) {
      return (Date) entryCreatedDate.clone();
    } else {
      return null;
    }
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
   * Needed to make sure the fields are interned correctly.
   *
   * @param is Input stream.
   * @throws IOException            if io error
   * @throws ClassNotFoundException if class not found
   */
  private void readObject(final ObjectInputStream is) throws IOException, ClassNotFoundException {
    is.defaultReadObject();
    entryPath = entryPath.intern();
    entryName = entryName.intern();
    entryLastAuthor = entryLastAuthor == null ? null : entryLastAuthor.intern();
  }

  /**
   * @return String representation of this object.
   */
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
