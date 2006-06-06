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
package de.berlios.sventon.service;

import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.repository.LogMessage;
import de.berlios.sventon.repository.cache.CacheException;

import java.util.List;

/**
 * Service class used to access the caches.
 *
 * @author jesper@users.berlios.de
 */
public interface CacheService {

  /**
   * Searches the cached entries for given string (name fragment).
   *
   * @param searchString String to search for
   * @return List of entries
   * @throws CacheException if error
   */
  List<RepositoryEntry> findEntry(final String searchString) throws CacheException;

  /**
   * Searches the cached entries for given string (name fragment) starting from given directory.
   *
   * @param searchString String to search for
   * @param startDir     Start path
   * @return List of entries
   * @throws CacheException if error
   */
  List<RepositoryEntry> findEntry(final String searchString, final String startDir) throws CacheException;

  /**
   * Searches the cached entries for given string (name fragment) starting from given directory.
   *
   * @param searchString String to search for
   * @param startDir     Start path
   * @param limit        Limit search result entries.
   * @return List of entries
   * @throws CacheException if error
   */
  List<RepositoryEntry> findEntry(final String searchString, final String startDir, final Integer limit) throws CacheException;

  /**
   * Searches the cached entries for all directories below given start dir.
   *
   * @param fromPath Start path
   * @return List of entries
   * @throws CacheException if error
   */
  List<RepositoryEntry> findDirectories(final String fromPath) throws CacheException;

  /**
   * Searches the cached commit messages for given string.
   *
   * @param queryString String to search for.
   * @return List of commit messages.
   * @throws CacheException if error
   */
  List<LogMessage> find(final String queryString) throws CacheException;

}
