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
package de.berlios.sventon.repository;

import de.berlios.sventon.config.ApplicationConfiguration;
import de.berlios.sventon.config.InstanceConfiguration;
import de.berlios.sventon.repository.cache.objectcache.ObjectCache;
import de.berlios.sventon.repository.cache.objectcache.ObjectCacheManager;
import de.berlios.sventon.service.RepositoryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Class to monitor repository changes.
 * During check, the latest revision number is fetched from the
 * repository and compared to the observable's latest revision.
 * If it differs, the revision delta will be fetched and published
 * to registered observers.
 *
 * @author jesper@user.berlios.de
 */
public class RevisionObservableImpl extends Observable implements RevisionObservable {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * The global application configuration.
   */
  private ApplicationConfiguration configuration;

  private boolean updating = false;

  /**
   * Object cache manager instance.
   * Used to get/put info regarding repository URL and last observed revision.
   */
  private ObjectCacheManager objectCacheManager;

  /**
   * Object cache key, <code>lastCachedLogRevision</code>
   */
  private static final String LAST_UPDATED_LOG_REVISION_CACHE_KEY = "lastCachedLogRevision";

  /**
   * The repository service instance.
   */
  private RepositoryService repositoryService;

  /**
   * Sets the repository service instance.
   *
   * @param repositoryService The service instance.
   */
  public void setRepositoryService(final RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

  /**
   * Constructor.
   *
   * @param observers List of observers to add
   */
  public RevisionObservableImpl(final List<RevisionObserver> observers) {
    logger.info("Starting revision observable");
    logger.debug("Adding [" + observers.size() + "] observers");
    for (final RevisionObserver revisionObserver : observers) {
      addObserver(revisionObserver);
    }
  }

  /**
   * Sets the application configuration.
   *
   * @param configuration ApplicationConfiguration
   */
  public void setConfiguration(final ApplicationConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Sets the object cache manager instance.
   *
   * @param objectCacheManager The cache manager instance.
   */
  public void setObjectCacheManager(final ObjectCacheManager objectCacheManager) {
    this.objectCacheManager = objectCacheManager;
  }

  /**
   * Update.
   *
   * @param instanceName The instance name.
   * @param repository   Repository to use for update.
   * @param objectCache  ObjectCache instance to read/write information about last observed revisions.
   */
  protected void update(final String instanceName, final SVNRepository repository, final ObjectCache objectCache) {
    if (configuration.isConfigured()) {
      updating = true;

      final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();

      try {
        Long lastUpdatedRevision = (Long) objectCache.get(LAST_UPDATED_LOG_REVISION_CACHE_KEY + instanceName);
        if (lastUpdatedRevision == null) {
          logger.info("No record about previously fetched revisions exists - fetching all revisions for instance: "
              + instanceName);
          lastUpdatedRevision = 0L;
        }

        final long headRevision = repositoryService.getLatestRevision(repository);

        if (headRevision > lastUpdatedRevision) {
          logEntries.addAll(repositoryService.getRevisions(repository, lastUpdatedRevision + 1, headRevision));
          logger.debug("Reading [" + logEntries.size() + "] revision(s) from instance: " + instanceName);
          setChanged();
          logger.debug("Notifying observers");
          notifyObservers(new RevisionUpdate(instanceName, logEntries));
          objectCache.put(LAST_UPDATED_LOG_REVISION_CACHE_KEY + instanceName, headRevision);
        }
      } catch (SVNException svnex) {
        throw new RuntimeException(svnex);
      } finally {
        updating = false;
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws RuntimeException if a subversion error occurs.
   */
  public synchronized void update(final String instanceName) {
    if (configuration.isConfigured()) {
      final InstanceConfiguration instanceConfiguration = configuration.getInstanceConfiguration(instanceName);
      if (instanceConfiguration.isCacheUsed()) {
        SVNRepository repository;
        try {
          repository = RepositoryFactory.INSTANCE.getRepository(instanceConfiguration);
          final ObjectCache objectCache = objectCacheManager.getCache(instanceName);
          update(instanceConfiguration.getInstanceName(), repository, objectCache);
        } catch (final Exception ex) {
          logger.warn("Unable to establish repository connection", ex);
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws RuntimeException if a subversion error occurs.
   */
  public synchronized void updateAll() {
    if (configuration.isConfigured()) {
      for (final String instanceName : configuration.getInstanceNames()) {
        update(instanceName);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public boolean isUpdating() {
    return updating;
  }

}
