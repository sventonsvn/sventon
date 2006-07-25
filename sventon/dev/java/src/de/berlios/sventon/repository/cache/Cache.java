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
package de.berlios.sventon.repository.cache;

import de.berlios.sventon.repository.LogMessage;
import de.berlios.sventon.repository.RepositoryEntry;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.List;

/**
 * Gateway class used to access the caches.
 *
 * @author jesper@users.berlios.de
 */
public interface Cache {

  /**
   * Searches the cached entries for given string (name fragment).
   *
   * @param searchString String to search for
   * @return List of entries
   * @throws CacheException if error
   */
  List<RepositoryEntry> findEntry(final String searchString) throws CacheException;

  /**
   * Searches the cached entries for given string CamelCase name.
   *
   * @param pattern CamelCase pattern to search for
   * @return List of entries
   * @throws CacheException if error
   */
  List<RepositoryEntry> findEntryByCamelCase(final CamelCasePattern pattern, final String startDir) throws CacheException;

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
   * Searches the cached log messages for given string.
   *
   * @param queryString String to search for.
   * @return List of log messages.
   * @throws CacheException if error
   */
  List<LogMessage> find(final String queryString) throws CacheException;

  /**
   * Gets a revision by number.
   *
   * @param revision Revision number of revision to get.
   * @return The revision info
   * @throws CacheException if error.
   *
   */
  SVNLogEntry getRevision(final long revision) throws CacheException;
}
