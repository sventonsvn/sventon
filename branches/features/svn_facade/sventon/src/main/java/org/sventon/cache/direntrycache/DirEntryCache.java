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
package org.sventon.cache.direntrycache;

import org.sventon.cache.Cache;
import org.sventon.cache.CacheException;
import org.sventon.model.CamelCasePattern;
import org.sventon.model.RepositoryEntry;

import java.util.List;
import java.util.Map;

/**
 * Contains the cached entries in the repository HEAD.
 *
 * @author jesper@sventon.org
 */
public interface DirEntryCache extends Cache {

  /**
   * Initializes the cache.
   *
   * @throws CacheException if unable to load cache.
   */
  public abstract void init() throws CacheException;

  /**
   * Shuts down the cache.
   */
  public abstract void shutdown();

  /**
   * Flushes the cache. Will only have effect on disk persistent caches.
   *
   * @throws CacheException if unable to flush cache.
   */
  public abstract void flush() throws CacheException;

  /**
   * Adds entries to the cache.
   *
   * @param entries The entries to parse and add
   */
  void add(final RepositoryEntry... entries);

  /**
   * Clears the entire cache.
   */
  void clear();

  /**
   * Removes entries from the cache, based by path and file name.
   *
   * @param pathAndName Entry to remove from cache.
   */
  void remove(String pathAndName);

  /**
   * Removes and adds entries in the same transaction.
   *
   * @param entriesToDelete Entries to delete.
   * @param entriesToAdd    Entries to add.
   */
  void update(final Map<String, RepositoryEntry.Kind> entriesToDelete, final List<RepositoryEntry> entriesToAdd);

  /**
   * Removes a directory entry including all children.
   *
   * @param pathAndName Directory entry to remove from cache.
   */
  void removeDirectory(final String pathAndName);

  /**
   * Finds entry names containing given search string.
   *
   * @param searchString Entry name search string.
   * @param startDir     Directory/path to start in.
   * @return List of entries with names (and/or author) matching given search string.
   */
  List<RepositoryEntry> findEntries(String searchString, String startDir);

  /**
   * Finds directories recursively.
   *
   * @param startPath Start path
   * @return List of entries (of type {@link org.sventon.model.RepositoryEntry.Kind#DIR}.
   */
  List<RepositoryEntry> findDirectories(final String startPath);

  /**
   * Gets the number of entries in cache.
   *
   * @return Count
   */
  int getSize();

  /**
   * Sets the latest cached revision number.
   * Used to determine if cache needs to be updated.
   *
   * @param revision Latest successfully cached revision number.
   */
  void setLatestCachedRevisionNumber(long revision);

  /**
   * Gets the latest cached revision number.
   * Used to determine if cache needs to be updated.
   *
   * @return Latest successfully cached revision number.
   */
  long getLatestCachedRevisionNumber();

  /**
   * Finds entries using camel case pattern
   *
   * @param camelCasePattern Camel case pattern
   * @param startPath        Start path
   * @return List of entries.
   */
  List<RepositoryEntry> findEntriesByCamelCasePattern(final CamelCasePattern camelCasePattern, final String startPath);
}
