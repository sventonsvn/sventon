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
package de.berlios.sventon.repository.cache.commitmessagecache;

import de.berlios.sventon.repository.cache.CacheException;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.List;

/**
 * Contains cached commit messages.
 * This class is a data holder, with various finder methods.
 *
 * @author jesper@users.berlios.de
 */
public interface CommitMessageCache {

  /**
   * Finds occurencies of given search string among the cached commit messages.
   *
   * @param searchString String to search for
   * @return List of something. Kind of depends on Lucene implementation. -- REVISIT
   * @throws de.berlios.sventon.repository.cache.CacheException
   */
  List<Object> find(final String searchString) throws CacheException;

  /**
   * Add one log entry to the cache.
   *
   * @param entry The entry to parse and add
   */
  void add(final SVNLogEntry entry);

  /**
   * Add one or more log entries to the cache.
   *
   * @param entries The entries to parse and add
   */
  void add(final SVNLogEntry... entries);

  /**
   * Gets the cached revision number.
   * Used to determine if cache needs to be updated.
   *
   * @return Cached revision number.
   */
  long getCachedRevision();

  /**
   * Sets the cached revision number.
   * Used if cache has been updated.
   *
   * @param revision Revision number.
   */
  void setCachedRevision(final long revision);

  /**
   * Clears the entire cache.
   */
  void clear();

  /**
   * Gets the repository URL.
   * Used to verifiy that the cache state and url matches.
   *
   * @return The URL to the repository.
   */
  String getRepositoryUrl();

  /**
   * Sets the repository URL.
   *
   * @param repositoryURL The URL
   */
  void setRepositoryURL(final String repositoryURL);

}
