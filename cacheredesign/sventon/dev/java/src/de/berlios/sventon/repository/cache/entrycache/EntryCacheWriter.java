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
package de.berlios.sventon.repository.cache.entrycache;

import de.berlios.sventon.repository.RepositoryEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jesper@users.berlios.de
 */
public class EntryCacheWriter {

  /**
   * The logging instance.
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * The EntryCache instance.
   */
  final private EntryCache entryCache;

  /**
   * Constructor.
   *
   * @param entryCache The EntryCache instance.
   */
  public EntryCacheWriter(final EntryCache entryCache) {
    this.entryCache = entryCache;
  }

  /**
   * Adds one entry to the cache.
   *
   * @param entry The entry to parse and add
   */
  public synchronized boolean add(final RepositoryEntry entry) {
    return entryCache.getEntries().add(entry);
  }

  /**
   * Adds one or more entries to the cache.
   *
   * @param entries The entries to parse and add
   */
  public synchronized boolean add(final List<RepositoryEntry> entries) {
    return entryCache.getEntries().addAll(entries);
  }

  /**
   * Removes entries from the cache, based by path and file name.
   *
   * @param pathAndName Entry to remove from cache.
   * @param recursive   True if remove should be performed recursively
   */
  public synchronized void removeByName(final String pathAndName, final boolean recursive) {
    final List<RepositoryEntry> toBeRemoved = new ArrayList<RepositoryEntry>();

    for (RepositoryEntry entry : entryCache.getEntries()) {
      if (recursive) {
        if (entry.getFullEntryName().startsWith(pathAndName)) {
          toBeRemoved.add(entry);
        }
      } else {
        if (entry.getFullEntryName().equals(pathAndName)) {
          entryCache.getEntries().remove(entry);
          return;
        }
      }
    }
    entryCache.getEntries().removeAll(toBeRemoved);
  }

  /**
   * Sets the repository URL.
   *
   * @param repositoryURL The URL
   */
  public synchronized void setRepositoryURL(final String repositoryURL) {
    entryCache.setRepositoryURL(repositoryURL);
  }

  /**
   * Sets the cached revision number.
   * Used if cache has been updated.
   *
   * @param revision Revision number.
   */
  public synchronized void setCachedRevision(final long revision) {
    entryCache.setCachedRevision(revision);
  }

  /**
   * Clears the entire cache.
   */
  public synchronized void clear() {
    entryCache.getEntries().clear();
  }

}
