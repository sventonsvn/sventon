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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.compass.annotations.Index;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;
import org.tmatesoft.svn.core.SVNDirEntry;

import java.io.Serializable;
import java.util.*;

/**
 * Represents an entry in the repository.
 *
 * @author jesper@sventon.org
 */
@Searchable(root = true)
public final class RepositoryEntry implements Serializable {

  public static final int FULL_ENTRY_NAME_MAX_LENGTH = 70;

  private static final long serialVersionUID = 3617229449081593805L;

  @SearchableId
  private String id;

  @SearchableProperty(index = Index.NOT_ANALYZED)
  private String path;

  @SearchableProperty
  private String name;

  @SearchableProperty
  private String camelCasePattern;

  @SearchableProperty
  private Kind kind;

  @SearchableProperty(format = "#000000000000")
  private long size;

  @SearchableProperty(format = "#000000000000")
  private long revision;

  @SearchableProperty
  private Date createdDate;

  @SearchableProperty
  private String lastAuthor;

  public enum Kind {
    DIR, FILE, NONE, UNKNOWN, ANY
  }

  /**
   * Default constructor.
   */
  private RepositoryEntry() {
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

    final String entryName = entry.getName();
    id = createId(entryPath, entry);
    try {
      camelCasePattern = CamelCasePattern.parse(entryName).getPattern();
    } catch (IllegalArgumentException e) {
      // ignore
    }
    copyEntry(entryPath, entry);
  }

  /**
   * Creates an Id.
   *
   * @param path  Path
   * @param entry SVN entry
   * @return Id
   */
  protected String createId(final String path, final SVNDirEntry entry) {
    return path + entry.getName();
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

  private void copyEntry(final String path, final SVNDirEntry entry) {
    this.path = path;
    this.lastAuthor = entry.getAuthor() == null ? null : entry.getAuthor();
    this.createdDate = entry.getDate();
    this.kind = Kind.valueOf(entry.getKind().toString().toUpperCase());
    this.name = entry.getName();
    this.revision = entry.getRevision();
    this.size = entry.getSize();
  }

  /**
   * Gets the entry name.
   *
   * @return The name.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the camel case pattern for this entry's name.
   *
   * @return Pattern
   */
  public CamelCasePattern getCamelCasePattern() {
    return new CamelCasePattern(camelCasePattern);
  }

  /**
   * Gets the entry name including full path.
   *
   * @return The name and full path.
   */
  public String getFullEntryName() {
    return path + name;
  }

  /**
   * Gets the full entry name in a display friendly format. <p/> The file name
   * and path will be abbreviated down to 60 characters.
   *
   * @return The abbreviated display friendly entry name
   * @see #FULL_ENTRY_NAME_MAX_LENGTH
   */
  public String getShortenedFullEntryName() {
    return getShortenedFullEntryName(FULL_ENTRY_NAME_MAX_LENGTH);
  }

  /**
   * Gets the full entry name in a display friendly format.
   *
   * @param maxLength Max path string length.
   * @return Path string, shortened if necessary.
   */
  protected String getShortenedFullEntryName(final int maxLength) {
    final String strippedPath = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
    final int maxWidth = maxLength - name.length() - 1;
    if (maxWidth > 3) {
      return StringUtils.abbreviate(strippedPath, maxWidth) + "/" + name;
    } else {
      return ".../" + name;
    }
  }

  /**
   * Gets the path.
   *
   * @return the path for this entry
   */
  public String getPath() {
    return path;
  }

  /**
   * Retrieves the entry syze in bytes.
   *
   * @return the size of this entry in bytes
   */
  public long getSize() {
    return size;
  }

  /**
   * Retrieves the entry node kind - whether it's a directory or file, for instance.
   *
   * @return the node kind of this entry. Can be <code>none</code>, <code>unknown</code>,
   *         <code>file</code> or <code>dir</code>.
   */
  public Kind getKind() {
    return kind;
  }

  /**
   * Returns the date the entry was created at.
   *
   * @return the creation date, or <tt>null</tt> if no date exists.
   */
  public Date getDate() {
    if (createdDate != null) {
      return (Date) createdDate.clone();
    } else {
      return null;
    }
  }

  /**
   * Gets the revision at which the entry was last modified in the repository.
   *
   * @return the revision of this entry when it was last changed
   */
  public long getRevision() {
    return revision;
  }

  /**
   * Retrieves the name of the person who was the last to update
   * this entry in the repository.
   *
   * @return the last author's name.
   */
  public String getAuthor() {
    return lastAuthor;
  }

  /**
   * @return String representation of this object.
   */
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
