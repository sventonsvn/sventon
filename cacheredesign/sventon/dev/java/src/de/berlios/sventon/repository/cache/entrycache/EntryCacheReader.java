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
import static de.berlios.sventon.repository.RepositoryEntry.Kind.any;
import de.berlios.sventon.repository.cache.CacheException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author jesper@users.berlios.de
 */
public class EntryCacheReader {

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
  public EntryCacheReader(final EntryCache entryCache) {
    this.entryCache = entryCache;
  }

  /**
   * Finds entry names based on given regex pattern.
   *
   * @param pattern Entry name pattern to search for
   * @return List of entries.
   * @throws CacheException if error
   */
  public List<RepositoryEntry> findByPattern(final String pattern, final RepositoryEntry.Kind kind, final Integer limit) throws CacheException {
    if (logger.isDebugEnabled()) {
      logger.debug("Finding [" + pattern + "] of kind [" + kind + "] with limit [" + limit + "]");
    }
    int count = 0;
    final List<RepositoryEntry> result = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);

    for (RepositoryEntry entry : entryCache.getEntries()) {
      if (entry.getFullEntryName().matches(pattern) && (entry.getKind() == kind || kind == any)) {
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

  /**
   * Gets the number of entries in cache.
   *
   * @return Count
   */
  public int getEntriesCount() {
    return entryCache.getEntries().size();
  }

  /**
   * Gets the cached revision number.
   * Used to determine if cache needs to be updated.
   *
   * @return Cached revision number.
   */
  public synchronized long getCachedRevision() {
    return entryCache.getCachedRevision();
  }

  /**
   * Gets the repository URL.
   * Used to verifiy that the cache state and url matches.
   *
   * @return The URL to the repository.
   */
  public synchronized String getRepositoryUrl() {
    return entryCache.getRepositoryUrl();
  }
}
