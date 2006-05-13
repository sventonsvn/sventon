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
import de.berlios.sventon.repository.cache.CacheException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;

/**
 * Contains a cached set of the repository entries for a specific revision and URL.
 *
 * @author jesper@users.berlios.de
 */
public abstract class EntryCache {

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
   * Cached URL.
   */
  private String repositoryURL;

  protected boolean initialized = false;

  /**
   * Checks if the cache has been properly initialized.
   *
   * @return True if initialized, false if not.
   */
  public synchronized boolean isInitialized() {
    return initialized;
  }

  /**
   * Shuts down the cache.
   *
   * @throws CacheException if unable to shut down cache instance.
   */
  public abstract void shutdown() throws CacheException;

  /**
   * Gets all entries in the cache.
   *
   * @return All entries currently in the cache
   */
  protected synchronized Set<RepositoryEntry> getEntries() {
    return cachedEntries;
  }

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
   * Sets the cached revision number.
   * Used if cache has been updated.
   *
   * @param revision Revision number.
   */
  protected synchronized void setCachedRevision(final long revision) {
    this.cachedRevision = revision;
  }

  /**
   * Gets the repository URL.
   * Used to verifiy that the cache state and url matches.
   *
   * @return The URL to the repository.
   */
  protected synchronized String getRepositoryUrl() {
    return repositoryURL;
  }

  /**
   * Sets the repository URL.
   *
   * @param repositoryURL The URL
   */
  protected synchronized void setRepositoryURL(final String repositoryURL) {
    this.repositoryURL = repositoryURL;
  }


}
