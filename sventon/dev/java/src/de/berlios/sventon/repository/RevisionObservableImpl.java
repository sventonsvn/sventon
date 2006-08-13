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

import de.berlios.sventon.cache.ObjectCache;
import de.berlios.sventon.config.ApplicationConfiguration;
import de.berlios.sventon.config.InstanceConfiguration;
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
   * Object cache instance. Used to get/put info regarding
   * repository URL and last observed revision.
   */
  private ObjectCache objectCache;

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
   * Sets the object cache instance.
   *
   * @param objectCache The cache instance.
   */
  public void setObjectCache(final ObjectCache objectCache) {
    this.objectCache = objectCache;
  }

  /**
   * Update.
   *
   * @param repository Repository to use for update.
   */
  protected void update(final SVNRepository repository, final String instanceName) {
    if (!configuration.isConfigured()) {
      // Silently return. sventon has not yet been configured.
      return;
    } else {
      updating = true;

      final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();

      try {
        Long lastUpdatedRevision = (Long) objectCache.get(LAST_UPDATED_LOG_REVISION_CACHE_KEY);
        if (lastUpdatedRevision == null) {
          logger.info("No record about previously fetched revisions exists - fetching all revisions");
          lastUpdatedRevision = 0L;
        }

        final long headRevision = repository.getLatestRevision();

        if (headRevision > lastUpdatedRevision) {
          logEntries.addAll(repositoryService.getRevisions(repository, lastUpdatedRevision + 1, headRevision));
          logger.debug("Reading [" + logEntries.size() + "] revision(s)");
          setChanged();
          logger.debug("Notifying observers");
          notifyObservers(logEntries);
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
  public synchronized void update() {
    if (!configuration.isConfigured()) {
      // Silently return. sventon has not yet been configured.
      return;
    }

    final InstanceConfiguration instanceConfiguration = configuration.getInstanceConfiguration("defaultsvn");
    if (!instanceConfiguration.isCacheUsed()) {
      // Silently return. Cache is disabled.
      return;
    } else {

      updating = true;

      SVNRepository repository = null;
      try {
        repository = RepositoryFactory.INSTANCE.getRepository(instanceConfiguration, configuration.getSVNConfigurationPath());
      } catch (SVNException svnex) {
        logger.warn("Unable to etablish repository connection", svnex);
        return;
      }
      update(repository, instanceConfiguration.getInstanceName());
    }
  }

  /**
   * {@inheritDoc}
   */
  public boolean isUpdating() {
    return updating;
  }

}
