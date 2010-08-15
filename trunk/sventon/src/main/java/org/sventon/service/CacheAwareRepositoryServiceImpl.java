/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
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
import org.sventon.SVNConnection;
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
 * Cache aware sub classed implementation of RepositoryService.
 *
 * @author jesper@sventon.org
 */
public final class CacheAwareRepositoryServiceImpl extends SVNKitRepositoryService {

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
   * If the instance is configured to use the cache, and the cache is not
   * currently busy updating, a cached log entry instance will be returned.
   */
  @Override
  public SVNLogEntry getLogEntry(final RepositoryName repositoryName, final SVNConnection connection, final long revision)
      throws SventonException {

    final SVNLogEntry logEntry;
    if (canReturnCachedRevisionsFor(repositoryName)) {
      logger.debug("Fetching cached revision: " + revision);
      logEntry = cacheGateway.getRevision(repositoryName, revision);
    } else {
      logEntry = super.getLogEntry(repositoryName, connection, revision);
    }
    return logEntry;
  }

  /**
   * If the instance is configured to use the cache, and the cache is not
   * currently busy updating, a cached log entry instance will be returned.
   */
  @Override
  public List<SVNLogEntry> getLogEntries(final RepositoryName repositoryName, final SVNConnection connection,
                                         final long fromRevision, final long toRevision, final String path,
                                         final long limit, final boolean stopOnCopy, boolean includeChangedPaths)
      throws SventonException {

    final SVNRepository repository = connection.getDelegate();
    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    if (canReturnCachedRevisionsFor(repositoryName)) {
      final List<Long> revisions = new ArrayList<Long>();
      if ("/".equals(path)) {
        // Requested path is root - simply return the revisions without checking with the repository
        revisions.addAll(calculateRevisionsToFetch(fromRevision, limit));
      } else {
        // To be able to return cached revisions, we first have to get the revision numbers for given path
        // Doing a logs-call, skipping the details, to get them.
        try {
          repository.log(new String[]{path}, fromRevision, toRevision, false, stopOnCopy, limit, new ISVNLogEntryHandler() {
            public void handleLogEntry(final SVNLogEntry logEntry) {
              revisions.add(logEntry.getRevision());
            }
          });
        } catch (SVNException ex) {
          throw new SventonException("Unable to get logs", ex);
        }
      }
      logger.debug("Fetching [" + limit + "] cached revisions: " + revisions);
      logEntries.addAll(cacheGateway.getRevisions(repositoryName, revisions));
    } else {
      logEntries.addAll(super.getLogEntries(repositoryName, connection, fromRevision, toRevision, path, limit,
          stopOnCopy, includeChangedPaths));
    }
    return logEntries;
  }

  protected List<Long> calculateRevisionsToFetch(final long fromRevision, final long limit) {
    final List<Long> revisions = new ArrayList<Long>();
    for (long i = fromRevision; i > (fromRevision - limit) && (i > 0); i--) {
      revisions.add(i);
    }
    return revisions;
  }

  private boolean canReturnCachedRevisionsFor(final RepositoryName repositoryName) {
    return application.getConfiguration(repositoryName).isCacheUsed() && !application.isUpdating(repositoryName);
  }

}
