/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.cache;

import org.sventon.model.LogMessage;
import org.sventon.model.RepositoryEntry;
import org.sventon.model.RepositoryName;
import org.tmatesoft.svn.core.SVNLogEntry;

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
  List<RepositoryEntry> findEntryByCamelCase(final RepositoryName repositoryName, final CamelCasePattern pattern, final String startDir) throws CacheException;

  /**
   * Searches the cached entries for given string (name fragment) starting from given directory.
   *
   * @param repositoryName Cache repository name
   * @param searchString   String to search for
   * @param startDir       Start path
   * @return List of entries
   * @throws CacheException if error
   */
  List<RepositoryEntry> findEntry(final RepositoryName repositoryName, final String searchString, final String startDir) throws CacheException;

  /**
   * Searches the cached entries for all directories below given start dir.
   *
   * @param repositoryName Repository name
   * @param fromPath       Start path
   * @return List of entries
   * @throws CacheException if error
   */
  List<RepositoryEntry> findDirectories(final RepositoryName repositoryName, final String fromPath) throws CacheException;

  /**
   * Searches the cached log messages for given string.
   *
   * @param repositoryName Repository name
   * @param queryString    String to search for.
   * @return List of log messages.
   * @throws CacheException if error
   */
  List<LogMessage> find(final RepositoryName repositoryName, final String queryString) throws CacheException;

  /**
   * Gets a revision by number.
   *
   * @param repositoryName Repository name
   * @param revision       Revision number of revision to get.
   * @return The revision info
   * @throws CacheException if error.
   */
  SVNLogEntry getRevision(final RepositoryName repositoryName, final long revision) throws CacheException;

  /**
   * Gets multiple revisions by number.
   *
   * @param repositoryName Repository name
   * @param revisions      List of revision numbers to get.
   * @return List containing the revisions
   * @throws CacheException if error.
   */
  List<SVNLogEntry> getRevisions(final RepositoryName repositoryName, final List<Long> revisions) throws CacheException;

}
