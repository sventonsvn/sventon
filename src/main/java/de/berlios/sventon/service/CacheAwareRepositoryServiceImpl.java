/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.service;

import de.berlios.sventon.SventonException;
import de.berlios.sventon.appl.Application;
import de.berlios.sventon.repository.cache.CacheGateway;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Cache aware subclassed implementation of RepositoryService.
 *
 * @author jesper@users.berlios.de
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
  public void setCacheGateway(final CacheGateway cacheGateway) {
    this.cacheGateway = cacheGateway;
  }

  /**
   * Sets the application.
   *
   * @param application Application
   */
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
  public SVNLogEntry getRevision(final String instanceName, final SVNRepository repository, final long revision)
      throws SVNException, SventonException {

    final SVNLogEntry logEntry;
    if (application.getInstance(instanceName).getConfiguration().isCacheUsed()
        && !application.getInstance(instanceName).isUpdatingCache()) {
      logger.debug("Fetching cached revision: " + revision);
      logEntry = cacheGateway.getRevision(instanceName, revision);
    } else {
      logEntry = super.getRevision(instanceName, repository, revision);
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
  public List<SVNLogEntry> getRevisions(final String instanceName, final SVNRepository repository,
                                        final long fromRevision, final long toRevision, final String path,
                                        final long limit) throws SVNException, SventonException {

    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    if (application.getInstance(instanceName).getConfiguration().isCacheUsed()
        && !application.getInstance(instanceName).isUpdatingCache()) {
      // To be able to return cached revisions, we first have to get the revision numbers
      // Doing a logs-call, skipping the details, to get them.
      final List<Long> revisions = new ArrayList<Long>();
      repository.log(new String[]{path}, fromRevision, toRevision, false, false, limit, new ISVNLogEntryHandler() {
        public void handleLogEntry(final SVNLogEntry logEntry) {
          revisions.add(logEntry.getRevision());
        }
      });
      logger.debug("Fetching [" + limit + "] cached revisions in the interval [" + toRevision + "-" + fromRevision + "]");
      logEntries.addAll(cacheGateway.getRevisions(instanceName, revisions));
    } else {
      logEntries.addAll(super.getRevisions(instanceName, repository, fromRevision, toRevision, path, limit));
    }
    return logEntries;
  }

}
