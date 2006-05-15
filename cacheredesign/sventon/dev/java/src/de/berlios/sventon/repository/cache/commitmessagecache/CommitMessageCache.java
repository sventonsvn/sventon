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
import de.berlios.sventon.repository.CommitMessage;

import java.util.List;

/**
 * Contains cached commit messages.
 *
 * @author jesper@users.berlios.de
 */
public interface CommitMessageCache {

  /**
   * Finds occurencies of given search string among the cached commit messages.
   *
   * @param queryString Index query string.
   * @return List of commit messages.
   * @throws de.berlios.sventon.repository.cache.CacheException
   *
   */
  List<CommitMessage> find(final String queryString) throws CacheException;

  /**
   * Add one commit message to the cache.
   *
   * @param commitMessage The commit message to cache.
   * @throws CacheException if error during index addition.
   */
  void add(final CommitMessage commitMessage) throws CacheException;

  /**
   * Gets the size of the commit message cache, i.e. the number
   * of cached messages.
   *
   * @return Cache size
   * @throws CacheException if unable to get cache size
   */
  int getSize() throws CacheException;

}
