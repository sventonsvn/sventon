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

  private RepositoryConfiguration configuration;
  private SVNRepository repository;
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
   * Sets the repository configuration.
   *
   * @param configuration Configuration
   */
  public void setRepositoryConfiguration(final RepositoryConfiguration configuration) {
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
   * For testing purposes only!
   *
   * @param repository Repository instance
   */
  protected void setRepository(final SVNRepository repository) {
    this.repository = repository;
  }

  /**
   * {@inheritDoc}
   *
   * @throws RuntimeException if a subversion error occurs.
   */
  public synchronized void update() {
    if (!configuration.isCacheUsed() || !isConnectionEstablished()) {
      // Silently return. sventon has not yet been configured.
      return;
    }

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
        //noinspection unchecked
        logEntries.addAll((List<SVNLogEntry>)
            repository.log(new String[]{"/"}, null, lastUpdatedRevision + 1, headRevision, true, false));
        logger.debug("Reading [" + logEntries.size() + "] revision(s)");
        setChanged();
        logger.debug("Notifying observers");
        notifyObservers(logEntries);
        objectCache.put(LAST_UPDATED_LOG_REVISION_CACHE_KEY, headRevision);
      }
    } catch (SVNException svnex) {
      throw new RuntimeException(svnex);
    } finally {
      updating = false;
    }
  }

  /**
   * {@inheritDoc}
   */
  public boolean isUpdating() {
    return updating;
  }

  /**
   * Checks if the repository connection is properly initialized.
   * If not, a connection will be created.
   */
  private boolean isConnectionEstablished() {
    if (repository == null) {
      try {
        logger.debug("Establishing repository connection");
        repository = RepositoryFactory.INSTANCE.getRepository(configuration);
      } catch (SVNException svne) {
        logger.warn("Could not establish repository connection", svne);
      }
      if (repository == null) {
        logger.info("Repository not configured yet");
        return false;
      }
    }
    return true;
  }

}
