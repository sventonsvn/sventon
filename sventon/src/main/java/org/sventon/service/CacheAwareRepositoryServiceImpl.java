/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.sventon.SventonException;
import org.sventon.appl.Application;
import org.sventon.cache.CacheGateway;
import org.sventon.model.RepositoryName;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Cache aware subclassed implementation of RepositoryService.
 *
 * @author jesper@sventon.org
 */
public final class CacheAwareRepositoryServiceImpl extends RepositoryServiceImpl {

  /**
   * The cache instance.
   */
  private CacheGateway cacheGateway;

  /**
   * The application.
   */
  private Application application;

  /**
   * Sets the cache gateway instance.
   *
   * @param cacheGateway Cache gateway instance
   */
  @Autowired
  public void setCacheGateway(final CacheGateway cacheGateway) {
    this.cacheGateway = cacheGateway;
  }

  /**
   * Sets the application.
   *
   * @param application Application
   */
  @Autowired
  public void setApplication(final Application application) {
    this.application = application;
  }

  /**
   * {@inheritDoc}
   * <p/>
   * If the instance is configured to use the cache, and the cache is not
   * currently busy updating, a cached log entry instance will be returned.
   */
  @Override
  public SVNLogEntry getRevision(final RepositoryName repositoryName, final SVNRepository repository, final long revision)
      throws SVNException, SventonException {

    final SVNLogEntry logEntry;
    if (canReturnCachedRevisionsFor(repositoryName)) {
      logger.debug("Fetching cached revision: " + revision);
      logEntry = cacheGateway.getRevision(repositoryName, revision);
    } else {
      logEntry = super.getRevision(repositoryName, repository, revision);
    }
    return logEntry;
  }

  /**
   * {@inheritDoc}
   * <p/>
   * If the instance is configured to use the cache, and the cache is not
   * currently busy updating, a cached log entry instance will be returned.
   */
  @Override
  public List<SVNLogEntry> getRevisions(final RepositoryName repositoryName, final SVNRepository repository,
                                        final long fromRevision, final long toRevision, final String path,
                                        final long limit, final boolean stopOnCopy) throws SVNException, SventonException {

    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    if (canReturnCachedRevisionsFor(repositoryName)) {
      final List<Long> revisions = new ArrayList<Long>();
      if ("/".equals(path)) {
        // Requested path is root - simply return the revisions without checking with the repository
        for (long i = fromRevision; i > fromRevision - limit; i--) {
          revisions.add(i);
        }
      } else {
        // To be able to return cached revisions, we first have to get the revision numbers for given path
        // Doing a logs-call, skipping the details, to get them.
        repository.log(new String[]{path}, fromRevision, toRevision, false, stopOnCopy, limit, new ISVNLogEntryHandler() {
          public void handleLogEntry(final SVNLogEntry logEntry) {
            revisions.add(logEntry.getRevision());
          }
        });
      }
      logger.debug("Fetching [" + limit + "] cached revisions: " + revisions);
      logEntries.addAll(cacheGateway.getRevisions(repositoryName, revisions));
    } else {
      logEntries.addAll(super.getRevisions(repositoryName, repository, fromRevision, toRevision, path, limit, stopOnCopy));
    }
    return logEntries;
  }

  private boolean canReturnCachedRevisionsFor(RepositoryName repositoryName) {
    return application.getRepositoryConfiguration(repositoryName).isCacheUsed() && !application.isUpdating(repositoryName);
  }

}
