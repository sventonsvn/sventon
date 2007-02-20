/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
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

import java.util.*;

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

  /**
   * Map to keep track of instances being updated.
   */
  private Set<String> updating = Collections.synchronizedSet(new HashSet<String>());

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
   * Maximum number of revisions to get each update.
   */
  private int maxRevisionCountPerUpdate;

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
   * Sets the repository service instance.
   *
   * @param repositoryService The service instance.
   */
  public void setRepositoryService(final RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
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
   * @param instanceName     The instance name.
   * @param repository       Repository to use for update.
   * @param objectCache      ObjectCache instance to read/write information about last observed revisions.
   * @param flushAfterUpdate If <tt>true</tt>, caches will be flushed after update.
   */
  protected void update(final String instanceName, final SVNRepository repository, final ObjectCache objectCache,
                        final boolean flushAfterUpdate) {

    updating.add(instanceName);

    try {
      Long lastUpdatedRevision = (Long) objectCache.get(LAST_UPDATED_LOG_REVISION_CACHE_KEY + instanceName);
      boolean clearCacheBeforeUpdate = false;

      if (lastUpdatedRevision == null) {
        logger.info("No record about previously fetched revisions exists - fetching all revisions for instance: "
            + instanceName);
        clearCacheBeforeUpdate = true;
        lastUpdatedRevision = 0L;
      }

      final long headRevision = repositoryService.getLatestRevision(repository);

      // Sanity check
      if (headRevision < lastUpdatedRevision) {
        final String errorMessage = "Repository HEAD revision (" + headRevision + ") is lower than last cached" +
            " revision. The repository URL has probably been changed. Delete all cache files from the temp directory" +
            " and restart sventon.";
        logger.error(errorMessage);
        throw new RuntimeException(errorMessage);
      }

      if (headRevision > lastUpdatedRevision) {
        long revisionsLeftToFetchCount = headRevision - lastUpdatedRevision;
        logger.debug("About to fetch [" + revisionsLeftToFetchCount + "] revisions");

        do {
          long fromRevision = lastUpdatedRevision + 1;
          long toRevision = revisionsLeftToFetchCount > maxRevisionCountPerUpdate ?
              lastUpdatedRevision + maxRevisionCountPerUpdate : headRevision;

          final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
          logEntries.addAll(repositoryService.getRevisionsFromRepository(repository, fromRevision, toRevision));
          logger.debug("Read [" + logEntries.size() + "] revision(s) from instance: " + instanceName);
          setChanged();
          final StringBuffer notification = new StringBuffer();
          notification.append("Notifying observers about [");
          notification.append(logEntries.size());
          notification.append("] revisions [");
          notification.append(fromRevision);
          notification.append("-");
          notification.append(toRevision);
          notification.append("]");
          logger.info(notification.toString());
          notifyObservers(new RevisionUpdate(instanceName, logEntries, flushAfterUpdate, clearCacheBeforeUpdate));

          lastUpdatedRevision = toRevision;
          logger.debug("Updating 'lastUpdatedRevision' to: " + lastUpdatedRevision);
          objectCache.put(LAST_UPDATED_LOG_REVISION_CACHE_KEY + instanceName, lastUpdatedRevision);

          revisionsLeftToFetchCount -= logEntries.size();
        } while (revisionsLeftToFetchCount > 0);
      }
    } catch (SVNException svnex) {
      throw new RuntimeException(svnex);
    } finally {
      updating.remove(instanceName);
    }

  }

  /**
   * {@inheritDoc}
   *
   * @throws RuntimeException if a subversion error occurs.
   */
  public void update(final String instanceName, final boolean flushAfterUpdate) {
    if (configuration.isConfigured()) {
      final InstanceConfiguration instanceConfiguration = configuration.getInstanceConfiguration(instanceName);
      if (instanceConfiguration.isCacheUsed()) {
        synchronized (instanceConfiguration) {
          SVNRepository repository;
          try {
            repository = RepositoryFactory.INSTANCE.getRepository(instanceConfiguration);
            final ObjectCache objectCache = objectCacheManager.getCache(instanceName);
            update(instanceConfiguration.getInstanceName(), repository, objectCache, flushAfterUpdate);
          } catch (final Exception ex) {
            logger.warn("Unable to establish repository connection", ex);
          }
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws RuntimeException if a subversion error occurs.
   */
  public void updateAll() {
    if (configuration.isConfigured()) {
      for (final String instanceName : configuration.getInstanceNames()) {
        if (!isUpdating(instanceName)) {
          update(instanceName, true);
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public boolean isUpdating(final String instanceName) {
    return updating.contains(instanceName);
  }

}
