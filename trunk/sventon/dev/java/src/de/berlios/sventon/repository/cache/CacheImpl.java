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
import static de.berlios.sventon.repository.RepositoryEntry.Kind.dir;
import de.berlios.sventon.repository.cache.entrycache.EntryCache;
import de.berlios.sventon.repository.cache.logmessagecache.LogMessageCache;
import de.berlios.sventon.repository.cache.revisioncache.RevisionCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.List;

/**
 * Gateway class used to access the caches.
 *
 * @author jesper@users.berlios.de
 */
public class CacheImpl implements Cache {

  private EntryCache entryCache;
  private LogMessageCache logMessageCache;
  private RevisionCache revisionCache;

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Constructor.
   */
  public CacheImpl() {
    logger.info("Starting cache");
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
   * Sets the revision cache instance.
   *
   * @param revisionCache The cache instance.
   */
  public void setRevisionCache(final RevisionCache revisionCache) {
    this.revisionCache = revisionCache;
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String searchString) throws CacheException {
    return entryCache.findByPattern("/" + ".*?" + searchString + ".*?", RepositoryEntry.Kind.any, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntryByCamelCase(final CamelCasePattern pattern, final String startDir) throws CacheException {
    return entryCache.findByPattern(".*" + startDir + pattern.getPattern(), RepositoryEntry.Kind.any, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String searchString, final String startDir) throws CacheException {
    return entryCache.findByPattern(startDir + ".*?" + searchString + ".*?", RepositoryEntry.Kind.any, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntry(final String searchString, final String startDir, final Integer limit) throws CacheException {
    return entryCache.findByPattern(startDir + ".*?" + searchString + ".*?", RepositoryEntry.Kind.any, limit);
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findDirectories(final String fromPath) throws CacheException {
    return entryCache.findByPattern(fromPath + ".*?", dir, null);
  }

  /**
   * {@inheritDoc}
   */
  public List<LogMessage> find(final String queryString) throws CacheException {
    return logMessageCache.find(queryString);
  }

  /**
   * {@inheritDoc}
   */
  public SVNLogEntry getRevision(final long revision) throws CacheException {
    return revisionCache.get(revision);
  }

}
