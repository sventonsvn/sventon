/*
 * ====================================================================
 * Copyright (c) 2005 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.index;

import de.berlios.sventon.ctrl.RepositoryEntry;
import de.berlios.sventon.svnsupport.RepositoryEntryComparator;
import static de.berlios.sventon.svnsupport.RepositoryEntryComparator.FULL_NAME;
import java.io.Serializable;
import java.util.*;

/**
 * The revision index keeps all repository entries cached for fast repository searching.
 * The RevisionIndex will be serialized to disk on exit and loaded on startup by
 * RevisionIndexer.
 *
 * @author jesper@users.berlios.de
 */
public class RevisionIndex implements Serializable {

  private static final long serialVersionUID = -6606585955661688509L;

  /**
   * The index.
   */
  private Set<RepositoryEntry> index;

  /**
   * Current indexed revision.
   */
  private long indexRevision = 0;

  /**
   * Indexed URL.
   */
  private String url = "";

  /**
   * Constructor.
   * 
   * @param url The url to index.
   */
  public RevisionIndex(final String url) {
    index = Collections.checkedSet(new TreeSet<RepositoryEntry>(new RepositoryEntryComparator(FULL_NAME, false)), RepositoryEntry.class);
    this.url = url;
  }

  /**
   * Gets the current index revision.
   *
   * @return The revision.
   */
  public long getIndexRevision() {
    return this.indexRevision;
  }

  /**
   * Sets the index revision.
   *
   * @param revision The revision
   */
  public void setIndexRevision(final long revision) {
    this.indexRevision = revision;
  }

  /**
   * Clears the index.
   */
  public void clearIndex() {
    index.clear();
  }

  /**
   * Gets the index entries.
   *
   * @return The index entries.
   */
  public Set<RepositoryEntry> getEntries() {
    return index;
  }

  /**
   * Adds an entry to the index.
   *
   * @param entry Entry to add
   * @return True if object added ok, false if not.
   */
  public boolean add(final RepositoryEntry entry) {
    return index.add(entry);
  }

  /**
   * Gets the index url.
   *
   * @return The index url
   */
  public String getUrl() {
    return this.url;
  }

  /**
   * Removes an entry from the index.
   * The index will be scanned for the given
   * path and remove the entry if found.
   *
   * @param path The full path to the entry to remove.
   * @param recursive Acts recursively if <code>true</code>
   */
  public void remove(final String path, final boolean recursive) {
    List<RepositoryEntry> toBeRemoved = new ArrayList<RepositoryEntry>();

    for (RepositoryEntry entry : index) {
      if (recursive) {
        if (entry.getFullEntryName().startsWith(path)) {
          toBeRemoved.add(entry);
        }
      } else {
        if (entry.getFullEntryName().equals(path)) {
          index.remove(entry);
          return;
        }
      }
    }
    index.removeAll(toBeRemoved);
  }

}
