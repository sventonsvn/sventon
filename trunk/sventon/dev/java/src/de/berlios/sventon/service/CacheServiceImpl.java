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

import de.berlios.sventon.repository.LogMessage;
import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.repository.RevisionObservable;
import static de.berlios.sventon.repository.RepositoryEntry.Kind.dir;
import de.berlios.sventon.repository.cache.CacheException;
import de.berlios.sventon.repository.cache.logmessagecache.LogMessageCache;
import de.berlios.sventon.repository.cache.entrycache.EntryCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Service class used to access the caches.
 *
 * @author jesper@users.berlios.de
 */
public class CacheServiceImpl implements CacheService {

  private LogMessageCache logMessageCache;
  private EntryCache entryCache;
  private RevisionObservable revisionObservable;

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Constructor.
   */
  public CacheServiceImpl() {
    logger.info("Starting cache service");
  }

  /**
   * Sets the observable. Needed to trigger cache updates.
   *
   * @param revisionObservable The observable
   */
  public void setRevisionObservable(final RevisionObservable revisionObservable) {
    this.revisionObservable = revisionObservable;
  }

  /**
   * Sets the entry cache instance.
   *
   * @param entryCache EntryCache instance.
   */
  public void setEntryCache(final EntryCache entryCache) {
    this.entryCache = entryCache;
  }

  /**
   * Sets the log message cache instance.
   *
   * @param logMessageCache The cache instance.
   */
  public void setLogMessageCache(final LogMessageCache logMessageCache) {
    this.logMessageCache = logMessageCache;
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String searchString) throws CacheException {
    revisionObservable.update();
    return entryCache.findByPattern("/" + ".*?" + searchString + ".*?", RepositoryEntry.Kind.any, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String searchString, final String startDir) throws CacheException {
    revisionObservable.update();
    return entryCache.findByPattern(startDir + ".*?" + searchString + ".*?", RepositoryEntry.Kind.any, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String searchString, final String startDir, final Integer limit) throws CacheException {
    revisionObservable.update();
    return entryCache.findByPattern(startDir + ".*?" + searchString + ".*?", RepositoryEntry.Kind.any, limit);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findDirectories(final String fromPath) throws CacheException {
    revisionObservable.update();
    return entryCache.findByPattern(fromPath + ".*?", dir, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<LogMessage> find(final String queryString) throws CacheException {
    revisionObservable.update();
    return logMessageCache.find(queryString);
  }

}
