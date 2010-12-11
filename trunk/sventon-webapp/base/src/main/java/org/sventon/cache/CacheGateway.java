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
package org.sventon.cache;

import org.sventon.model.CamelCasePattern;
import org.sventon.model.DirEntry;
import org.sventon.model.LogMessageSearchItem;
import org.sventon.model.RepositoryName;

import java.util.List;

/**
 * Gateway class used to access the caches.
 *
 * @author jesper@sventon.org
 */
public interface CacheGateway {

  /**
   * Searches the cached entries for given string CamelCase name.
   *
   * @param repositoryName Repository name
   * @param pattern        CamelCase pattern to search for
   * @param startDir       Directory where to start search
   * @return List of entries
   * @throws CacheException if error
   */
  List<DirEntry> findEntriesByCamelCase(final RepositoryName repositoryName, final CamelCasePattern pattern,
                                        final String startDir) throws CacheException;

  /**
   * Searches the cached entries for given string (name fragment) starting from given directory.
   *
   * @param repositoryName Cache repository name
   * @param searchString   String to search for
   * @param startDir       Start path
   * @return List of entries with names (and/or author) matching given search string.
   * @throws CacheException if error
   */
  List<DirEntry> findEntries(final RepositoryName repositoryName, final String searchString,
                             final String startDir) throws CacheException;

  /**
   * Searches the cached entries for all directories below given start dir.
   *
   * @param repositoryName Repository name
   * @param fromPath       Start path
   * @return List of entries
   * @throws CacheException if error
   */
  List<DirEntry> findDirectories(final RepositoryName repositoryName, final String fromPath) throws CacheException;

  /**
   * Searches the cached log entries for given string.
   *
   * @param repositoryName Repository name
   * @param queryString    String to search for.
   * @return List of log entries.
   * @throws CacheException if error
   */
  List<LogMessageSearchItem> find(final RepositoryName repositoryName, final String queryString) throws CacheException;

  /**
   * Searches the cached log entries for given string.
   *
   * @param repositoryName Repository name
   * @param queryString    String to search for.
   * @param startDir       Directory/path to start in.
   * @return List of log entries.
   * @throws CacheException if error
   */
  List<LogMessageSearchItem> find(final RepositoryName repositoryName, final String queryString, final String startDir)
      throws CacheException;

}
