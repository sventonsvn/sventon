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
package org.sventon.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.sventon.SVNConnection;
import org.sventon.SVNConnectionFactory;
import org.sventon.SventonException;
import org.sventon.appl.Application;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.cache.ObjectCacheManager;
import org.sventon.cache.objectcache.ObjectCache;
import org.sventon.model.LogEntry;
import org.sventon.model.RepositoryName;
import org.sventon.service.RepositoryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to monitor repository changes.
 * During check, the latest revision number is fetched from the
 * repository and compared to the monitor's latest revision.
 * If it differs, the revision delta will be fetched and published to registered listeners.
 *
 * @author jesper@sventon.org
 */
@ManagedResource
public final class RepositoryChangeMonitorImpl implements RepositoryChangeMonitor {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * The application.
   */
  private Application application;

  /**
   * Object cache manager instance.
   * Used to get/put info regarding repository URL and last observed revision.
   */
  private ObjectCacheManager objectCacheManager;

  /**
   * Object cache key, <code>lastCachedLogRevision</code>.
   */
  private static final String LAST_UPDATED_REVISION_CACHE_KEY = "lastCachedRevision";

  /**
   * Cache format version. If incremented, caches will be scratched and rebuilt.
   */
  private static final int CACHE_FORMAT_VERSION = 1;

  /**
   * Maximum number of revisions to get each update.
   */
  private int maxRevisionCountPerUpdate;

  /**
   * Service.
   */
  private RepositoryService repositoryService;

  /**
   * Repository factory.
   */
  private SVNConnectionFactory connectionFactory;

  /**
   * Change listeners that will be notified if a new repository revision is detected.
   */
  private List<RepositoryChangeListener> changeListeners = new ArrayList<RepositoryChangeListener>();

  /**
   * Constructor.
   */
  public RepositoryChangeMonitorImpl() {
    logger.info("Starting repository monitor");
  }

  /**
   * @param listeners List of listeners to add
   */
  public synchronized void setListeners(final List<RepositoryChangeListener> listeners) {
    for (final RepositoryChangeListener repositoryChangeListener : listeners) {
      logger.debug("Adding listener: " + repositoryChangeListener.getClass().getName());
      changeListeners.add(repositoryChangeListener);
    }
  }

  @Override
  public void update(final RepositoryName repositoryName) {
    if (application.isConfigured()) {
      final RepositoryConfiguration configuration = application.getConfiguration(repositoryName);

      if (configuration.isCacheUsed() && !application.isUpdating(repositoryName)) {
        application.setUpdatingCache(repositoryName, true);
        SVNConnection connection = null;
        try {
          connection = connectionFactory.createConnection(configuration.getName(),
              configuration.getSVNURL(), configuration.getCacheCredentials());
          final ObjectCache objectCache = objectCacheManager.getCache(repositoryName);
          update(repositoryName, connection, objectCache);
        } catch (final Exception ex) {
          logger.warn("Unable to establish repository connection", ex);
        } finally {
          if (connection != null) {
            connection.closeSession();
          }
          application.setUpdatingCache(repositoryName, false);
        }
      }
    }
  }

  @Override
  @ManagedOperation
  public void updateAll() {
    if (application.isConfigured()) {
      for (final RepositoryName repositoryName : application.getRepositoryNames()) {
        update(repositoryName);
      }
    }
  }

  /**
   * Update.
   *
   * @param name        The Repository name
   * @param connection  Repository to use for update.
   * @param objectCache ObjectCache instance to read/write information about last observed revisions.
   */
  protected void update(final RepositoryName name, final SVNConnection connection, final ObjectCache objectCache) {

    try {
      final long headRevision = repositoryService.getLatestRevision(connection);
      final String lastUpdatedKeyName = getLastUpdatedKeyName(name);
      Long lastUpdatedRevision = (Long) objectCache.get(lastUpdatedKeyName);

      boolean clearCacheBeforeUpdate = false;

      if (lastUpdatedRevision == null) {
        logger.info("No record about previously fetched revisions exists - fetching all revisions for repository: " + name);
        clearCacheBeforeUpdate = true;
        lastUpdatedRevision = 0L;
      }

      handleSuspectedUrlChange(lastUpdatedRevision, headRevision);

      if (headRevision > lastUpdatedRevision) {
        long revisionsLeftToFetchCount = headRevision - lastUpdatedRevision;
        logger.debug("About to fetch [" + revisionsLeftToFetchCount + "] revisions from repository: " + name);

        do {
          final long fromRevision = lastUpdatedRevision + 1;
          final long toRevision = revisionsLeftToFetchCount > maxRevisionCountPerUpdate
              ? lastUpdatedRevision + maxRevisionCountPerUpdate : headRevision;

          final List<LogEntry> logEntries = new ArrayList<LogEntry>();
          logEntries.addAll(repositoryService.getLogEntriesFromRepositoryRoot(connection, fromRevision, toRevision));
          logger.debug("Read [" + logEntries.size() + "] revision(s) from repository: " + name);
          logger.info(createNotificationLogMessage(fromRevision, toRevision, logEntries.size()));
          notifyListeners(new RevisionUpdate(name, logEntries, clearCacheBeforeUpdate));
          lastUpdatedRevision = toRevision;
          logger.debug("Updating 'lastUpdatedRevision' to: " + lastUpdatedRevision);
          objectCache.put(lastUpdatedKeyName, lastUpdatedRevision);
          objectCache.flush();
          clearCacheBeforeUpdate = false;
          revisionsLeftToFetchCount -= logEntries.size();
        } while (revisionsLeftToFetchCount > 0);
      }
    } catch (SventonException svnex) {
      logger.warn("Exception: " + svnex.getMessage());
      logger.debug("Exception [" + svnex.toString() + "]", svnex);
    }
  }

  private void notifyListeners(final RevisionUpdate revisionUpdate) {
    for (RepositoryChangeListener changeListener : changeListeners) {
      changeListener.update(revisionUpdate);
    }
  }

  private String getLastUpdatedKeyName(final RepositoryName name) {
    return LAST_UPDATED_REVISION_CACHE_KEY + "_" + CACHE_FORMAT_VERSION + "_" + name;
  }

  /**
   * Sanity check of revision numbers.
   *
   * @param lastUpdatedRevision Last updated revision number
   * @param headRevision        Current head revision number.
   * @throws IllegalStateException if last updated revision is greater than head revision.
   */
  private void handleSuspectedUrlChange(final long lastUpdatedRevision, long headRevision) {
    if (headRevision < lastUpdatedRevision) {
      final String errorMessage = "Repository HEAD revision (" + headRevision + ") is lower than last cached"
          + " revision. The repository URL has probably been changed. Delete all cache files from the temp directory"
          + " and restart sventon.";
      logger.error(errorMessage);
      throw new IllegalStateException(errorMessage);
    }
  }

  /**
   * Creates an informative string for logging listener notifications.
   *
   * @param fromRevision    From revision
   * @param toRevision      To revision
   * @param logEntriesCount Number of log entries.
   * @return String intended for logging.
   */
  private String createNotificationLogMessage(long fromRevision, long toRevision, int logEntriesCount) {
    final StringBuffer notification = new StringBuffer();
    notification.append("Notifying listeners about [");
    notification.append(logEntriesCount);
    notification.append("] revisions [");
    notification.append(fromRevision);
    notification.append("-");
    notification.append(toRevision);
    notification.append("]");
    return notification.toString();
  }

  /**
   * Sets the repository service instance.
   *
   * @param repositoryService The service instance.
   */
  @Autowired
  public void setRepositoryService(final RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

  /**
   * Sets the repository connection factory instance.
   *
   * @param connectionFactory Factory instance.
   */
  @Autowired
  public void setConnectionFactory(final SVNConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  /**
   * Sets the maximum number of revisions to get (and update) each time.
   * If will hopefully decrease the memory consumption during cache loading
   * on big repositories.
   *
   * @param count Max revisions per update.
   */
  public void setMaxRevisionCountPerUpdate(final int count) {
    this.maxRevisionCountPerUpdate = count;
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
   * Sets the object cache manager instance.
   *
   * @param objectCacheManager The cache manager instance.
   */
  @Autowired
  public void setObjectCacheManager(final ObjectCacheManager objectCacheManager) {
    this.objectCacheManager = objectCacheManager;
  }

}
