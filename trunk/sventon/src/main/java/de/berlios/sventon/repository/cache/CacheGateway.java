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
public interface CacheGateway {

  /**
   * Searches the cached entries for given string CamelCase name.
   *
   * @param instanceName Cache instance name
   * @param pattern      CamelCase pattern to search for
   * @param startDir     Directory where to start search
   * @return List of entries
   * @throws CacheException if error
   */
  List<RepositoryEntry> findEntryByCamelCase(final String instanceName, final CamelCasePattern pattern, final String startDir) throws CacheException;

  /**
   * Searches the cached entries for given string (name fragment) starting from given directory.
   *
   * @param instanceName Cache instance name
   * @param searchString String to search for
   * @param startDir     Start path
   * @return List of entries
   * @throws CacheException if error
   */
  List<RepositoryEntry> findEntry(final String instanceName, final String searchString, final String startDir) throws CacheException;

  /**
   * Searches the cached entries for all directories below given start dir.
   *
   * @param instanceName Cache instance name
   * @param fromPath     Start path
   * @return List of entries
   * @throws CacheException if error
   */
  List<RepositoryEntry> findDirectories(final String instanceName, final String fromPath) throws CacheException;

  /**
   * Searches the cached log messages for given string.
   *
   * @param instanceName Cache instance name
   * @param queryString  String to search for.
   * @return List of log messages.
   * @throws CacheException if error
   */
  List<LogMessage> find(final String instanceName, final String queryString) throws CacheException;

  /**
   * Gets a revision by number.
   *
   * @param instanceName Cache instance name
   * @param revision     Revision number of revision to get.
   * @return The revision info
   * @throws CacheException if error.
   */
  SVNLogEntry getRevision(final String instanceName, final long revision) throws CacheException;

  /**
   * Gets multiple revisions by number.
   *
   * @param instanceName Cache instance name
   * @param revisions    List of revision numbers to get.
   * @return List containing the revisions
   * @throws CacheException if error.
   */
  List<SVNLogEntry> getRevisions(final String instanceName, final List<Long> revisions) throws CacheException;

}
