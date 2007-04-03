/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
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
import static de.berlios.sventon.repository.RepositoryEntry.Kind.any;
import de.berlios.sventon.repository.cache.CacheException;
import de.berlios.sventon.repository.cache.Cache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Contains a cached set of the repository entries for a specific revision and URL.
 *
 * @author jesper@users.berlios.de
 */
public abstract class EntryCache implements Cache {

  /**
   * The logging instance.
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * The cached entries.
   */
  private Set<RepositoryEntry> cachedEntries;

  /**
   * Cached revision.
   */
  private long cachedRevision = 0;

  /**
   * Shuts down the cache.
   *
   * @throws CacheException if unable to shut down cache instance.
   */
  public abstract void shutdown() throws CacheException;

  /**
   * Flushes the cache. Will only have effect on disk persistent caches.
   *
   * @throws CacheException
   */
  public abstract void flush() throws CacheException;

  /**
   * Sets the entries.
   *
   * @param entries Entries
   */
  protected synchronized void setEntries(final Set<RepositoryEntry> entries) {
    this.cachedEntries = entries;
  }

  /**
   * Gets the cached revision number.
   * Used to determine if cache needs to be updated.
   *
   * @return Cached revision number.
   */
  protected synchronized long getCachedRevision() {
    return cachedRevision;
  }

  /**
   * Gets the set of the cached entries.
   *
   * @return Set of cached entries
   */
  protected synchronized Set<RepositoryEntry> getCachedEntries() {
    return cachedEntries;
  }

  /**
   * Sets the cached revision number.
   * Used if cache has been updated.
   *
   * @param revision Revision number.
   */
  protected synchronized void setCachedRevision(final long revision) {
    this.cachedRevision = revision;
  }

  /**
   * Gets the number of entries in cache.
   *
   * @return Count
   */
  public int getSize() {
    return cachedEntries.size();
  }

  /**
   * Adds one entry to the cache.
   *
   * @param entry The entry to parse and add
   */
  public synchronized boolean add(final RepositoryEntry entry) {
    return cachedEntries.add(entry);
  }

  /**
   * Adds one or more entries to the cache.
   *
   * @param entries The entries to parse and add
   */
  public synchronized boolean add(final List<RepositoryEntry> entries) {
    return cachedEntries.addAll(entries);
  }

  /**
   * Removes entries from the cache, based by path and file name.
   *
   * @param pathAndName Entry to remove from cache.
   * @param recursive   True if remove should be performed recursively
   */
  public synchronized void removeByName(final String pathAndName, final boolean recursive) {
    final List<RepositoryEntry> toBeRemoved = new ArrayList<RepositoryEntry>();

    for (RepositoryEntry entry : cachedEntries) {
      if (recursive) {
        if (entry.getFullEntryName().startsWith(pathAndName)) {
          toBeRemoved.add(entry);
        }
      } else {
        if (entry.getFullEntryName().equals(pathAndName)) {
          toBeRemoved.add(entry);
          break;
        }
      }
    }
    cachedEntries.removeAll(toBeRemoved);
  }

  /**
   * Clears the entire cache.
   */
  public synchronized void clear() {
    cachedEntries.clear();
  }

  /**
   * Finds entry names based on given regex pattern.
   *
   * @param pattern Entry name pattern to search for
   * @return List of entries.
   * @throws CacheException if error
   */
  public synchronized List<RepositoryEntry> findByPattern(final Pattern pattern, final RepositoryEntry.Kind kind, final Integer limit) throws CacheException {
    if (logger.isDebugEnabled()) {
      logger.debug("Finding [" + pattern + "] of kind [" + kind + "] with limit [" + limit + "]");
    }
    int count = 0;
    final List<RepositoryEntry> result = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);

    for (final RepositoryEntry entry : cachedEntries) {
      final Matcher matcher = pattern.matcher(entry.getFullEntryName());
      if (matcher.matches() && (entry.getKind() == kind || kind == any)) {
        result.add(entry);
        if (limit != null && ++count == limit) {
          break;
        }
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug("Result count: " + result.size());
      logger.debug("Result: " + result);
    }
    return result;
  }

}
