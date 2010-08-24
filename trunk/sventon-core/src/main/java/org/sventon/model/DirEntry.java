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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Represents an entry in the repository.
 *
 * @author jesper@sventon.org
 */
@Searchable(root = true)
public final class DirEntry implements Serializable {

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
  private DirEntry() {
  }



  public DirEntry(final String entryPath, final String name, final String author, final Date date, final Kind kind, long revision, long size) {
    if (entryPath == null) {
      throw new IllegalArgumentException("entryPath cannot be null.");
    }

    if (name == null) {
      throw new IllegalArgumentException("name cannot be null.");
    }

    id = entryPath + name;

    try {
      camelCasePattern = CamelCasePattern.parse(name).getPattern();
    } catch (IllegalArgumentException e) {
      // ignore
    }

    this.path = entryPath;
    this.lastAuthor = author;
    this.createdDate = date;
    this.kind = kind;
    this.name = name;
    this.revision = revision;
    this.size = size;
  }




  /**
   * Creates a collection of <code>DirEntry</code> objects based
   * on given collection of <code>DirEntry</code> instances and <code>Properties</code>.
   *
   * @param entries  List of DirEntry.
   * @param properties The Properties for these entries
   * @return The directory list instance containing a List<DirEntry> and Properties.
   */
  public static DirList createDirectoryList(final List<DirEntry> entries, Properties properties) {
    return new DirList(entries, properties);
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

  @Override
  public String toString() {
    return "DirEntry{" +
        "path='" + path + '\'' +
        ", name='" + name + '\'' +
        ", kind=" + kind +
        ", camelCasePattern='" + camelCasePattern + '\'' +
        ", size=" + size +
        ", revision=" + revision +
        ", createdDate=" + createdDate +
        ", lastAuthor='" + lastAuthor + '\'' +
        '}';
  }
}
