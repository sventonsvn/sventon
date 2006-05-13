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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.List;

/**
 * Contains cached commit messages.
 * This implementation should use Lucene internally.
 * This class is a data holder only, with various finder methods.
 *
 * @author jesper@users.berlios.de
 */
public class CommitMessageCacheImpl implements CommitMessageCache {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Current cached revision.
   */
  private long cachedRevision = 0;

  /**
   * Cached URL.
   */
  private String repositoryURL;

  /**
   * Constructor.
   */
  public CommitMessageCacheImpl() {
    logger.debug("Initializing cache");
  }

  /**
   * {@inheritDoc}
   */
  public List<Object> find(final String searchString) throws CacheException {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public synchronized void add(final SVNLogEntry entry) {
  }

  /**
   * {@inheritDoc}
   */
  public synchronized void add(final SVNLogEntry... entries) {
  }

  /**
   * {@inheritDoc}
   */
  public long getCachedRevision() {
    return cachedRevision;
  }

  /**
   * {@inheritDoc}
   */
  public synchronized void setCachedRevision(final long revision) {
    this.cachedRevision = revision;
  }

  /**
   * {@inheritDoc}
   */
  public String getRepositoryUrl() {
    return repositoryURL;
  }

  /**
   * {@inheritDoc}
   */
  public synchronized void setRepositoryURL(final String repositoryURL) {
    this.repositoryURL = repositoryURL;
  }

  /**
   * {@inheritDoc}
   */
  public synchronized void clear() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

}
