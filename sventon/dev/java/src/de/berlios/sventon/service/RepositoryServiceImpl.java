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

import de.berlios.sventon.repository.RepositoryConfiguration;
import de.berlios.sventon.repository.cache.CacheException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for accessing the subversion repository.
 *
 * @author jesper@users.berlios.de
 */
public class RepositoryServiceImpl implements RepositoryService {

  /**
   * Logger for this class and subclasses.
   */
  protected final Log logger = LogFactory.getLog(getClass());

  /**
   * The repository configuration. Used to check whether caching is enabled or not.
   */
  private RepositoryConfiguration configuration;

  /**
   * The cache service instance.
   */
  private CacheService cacheService;

  /**
   * {@inheritDoc}
   */
  public SVNLogEntry getRevision(final SVNRepository repository, final long revision)
      throws SVNException {

    SVNLogEntry entry = null;
    if (configuration.isCacheUsed()) {
      try {
        logger.debug("Getting cached revision: " + revision);
        entry = getCachedRevision(revision);
      } catch (CacheException ce) {
        logger.warn("Unable to get cache revision: " + revision, ce);
        logger.info("Fallback - make a deep log fetch instead");
      }
    }
    return entry != null ? entry : getRevision(repository, revision, "/");
  }

  /**
   * {@inheritDoc}
   */
  public SVNLogEntry getRevision(final SVNRepository repository, final long revision, final String path)
      throws SVNException {

    return (SVNLogEntry) repository.log(
        new String[]{path}, null, revision, revision, true, false).iterator().next();
  }

  /**
   * {@inheritDoc}
   */
  public List<SVNLogEntry> getRevisions(final SVNRepository repository, final long fromRevision, final long toRevision)
      throws SVNException {

    return getRevisions(repository, fromRevision, toRevision, "/", -1);
  }

  /**
   * {@inheritDoc}
   */
  public List<SVNLogEntry> getRevisions(final SVNRepository repository, final long fromRevision, final long toRevision,
                                        final long limit) throws SVNException {

    return getRevisions(repository, fromRevision, toRevision, "/", limit);
  }

  /**
   * {@inheritDoc}
   */
  public List<SVNLogEntry> getRevisions(final SVNRepository repository, final long fromRevision, final long toRevision,
                                        final String path, final long limit) throws SVNException {

    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    repository.log(new String[]{path}, fromRevision, toRevision, true, false, limit, new ISVNLogEntryHandler() {
      public void handleLogEntry(final SVNLogEntry logEntry) throws SVNException {
        logEntries.add(logEntry);
      }
    });
    return logEntries;
  }

  private SVNLogEntry getCachedRevision(final long revision) throws CacheException {
    return cacheService.getRevision(revision);
  }

  private List<SVNLogEntry> getCachedRevisions(final long fromRevision, final long toRevision) throws CacheException {
    //TODO: revisit!
    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    if (fromRevision < toRevision) {
      for (long i = fromRevision; i <= toRevision; i++) {
        logEntries.add(cacheService.getRevision(i));
      }
    } else {
      for (long i = fromRevision; i >= toRevision; i--) {
        logEntries.add(cacheService.getRevision(i));
      }
    }
    return logEntries;
  }

  /**
   * Set repository configuration.
   *
   * @param configuration Configuration
   */
  public void setRepositoryConfiguration(final RepositoryConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Sets the cache service instance.
   *
   * @param cacheService Cache instance
   */
  public void setCacheService(final CacheService cacheService) {
    this.cacheService = cacheService;
  }

}
