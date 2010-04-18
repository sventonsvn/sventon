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
package org.sventon.cache.entrycache;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.compass.core.*;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassEnvironment;
import org.sventon.cache.CacheException;
import org.sventon.model.CamelCasePattern;
import org.sventon.model.RepositoryEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * EntryCacheImpl.
 */
public final class EntryCacheImpl implements EntryCache {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Default filename for storing the latest cached revision number.
   */
  private static final String ENTRY_CACHE_FILENAME = "entrycache.ser";

  /**
   * The cache file.
   */
  private File latestCachedRevisionFile;

  /**
   * Cached revision.
   */
  private final AtomicLong cachedRevision = new AtomicLong(0);

  private final CompassConfiguration compassConfiguration = new CompassConfiguration();

  private final File cacheDirectory;

  private Compass compass;

  private final boolean useDiskStore;

  /**
   * Constructs an in-memory cache instance.
   *
   * @param cacheRootDirectory Cache root directory
   */
  public EntryCacheImpl(final File cacheRootDirectory) {
    this(cacheRootDirectory, false);
  }

  /**
   * Constructor.
   *
   * @param cacheDirectory Cache directory
   * @param useDiskStore   If true index will be stored to disk. Otherwise it will be kept in memory.
   */
  public EntryCacheImpl(final File cacheDirectory, final boolean useDiskStore) {
    this.cacheDirectory = cacheDirectory;
    this.useDiskStore = useDiskStore;
  }

  /**
   * {@inheritDoc}
   */
  public void init() throws CacheException {
    final String connectionString;

    if (useDiskStore) {
      connectionString = cacheDirectory.getAbsolutePath();
    } else {
      connectionString = "ram://" + cacheDirectory.getAbsolutePath();
    }

    compassConfiguration.setSetting(CompassEnvironment.CONNECTION, connectionString)
        .setSetting(CompassEnvironment.DEBUG, String.valueOf(true))
        .setSetting(CompassEnvironment.NAME, cacheDirectory.getParent())
        .setSetting("compass.engine.queryParser.default.type", CustomizedLuceneQueryParser.class.getName())
        .addClass(RepositoryEntry.class);
    compass = compassConfiguration.buildCompass();
    latestCachedRevisionFile = new File(cacheDirectory, ENTRY_CACHE_FILENAME);

    if (useDiskStore) {
      loadLatestCachedRevisionNumber();
    }
  }

  /**
   * {@inheritDoc}
   */
  public void shutdown() {
    compass.close();
  }

  /**
   * {@inheritDoc}
   */
  public void setLatestCachedRevisionNumber(final long revision) {
    this.cachedRevision.set(revision);
  }

  /**
   * {@inheritDoc}
   */
  public long getLatestCachedRevisionNumber() {
    return cachedRevision.get();
  }

  /**
   * {@inheritDoc}
   */
  public int getSize() {
    final CompassTemplate template = new CompassTemplate(compass);
    return template.execute(new CompassCallback<Integer>() {
      public Integer doInCompass(final CompassSession session) {
        final CompassHits compassHits = session.queryBuilder().matchAll().hits();
        return compassHits.length();
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  public void add(final RepositoryEntry... entries) {
    final CompassTemplate template = new CompassTemplate(compass);
    template.execute(new CompassCallbackWithoutResult() {
      protected void doInCompassWithoutResult(CompassSession session) {
        for (RepositoryEntry entry : entries) {
          session.save(entry);
        }
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  public void clear() {
    final CompassTemplate template = new CompassTemplate(compass);
    template.execute(new CompassCallbackWithoutResult() {
      protected void doInCompassWithoutResult(final CompassSession session) {
        session.delete(session.queryBuilder().matchAll());
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  public void remove(final String pathAndName) {
    final CompassTemplate template = new CompassTemplate(compass);
    template.execute(new CompassCallbackWithoutResult() {
      protected void doInCompassWithoutResult(CompassSession session) {
        session.delete(RepositoryEntry.class, pathAndName);
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  public void update(final Map<String, RepositoryEntry.Kind> entriesToDelete,
                     final List<RepositoryEntry> entriesToAdd) {

    final CompassTemplate template = new CompassTemplate(compass);
    template.execute(new CompassCallbackWithoutResult() {
      protected void doInCompassWithoutResult(final CompassSession session) {
        logger.debug("Applying changes inside transaction...");

        // Apply deletion...
        for (String id : entriesToDelete.keySet()) {
          final RepositoryEntry.Kind kind = entriesToDelete.get(id);
          if (kind == RepositoryEntry.Kind.DIR) {
            // Directory node deleted
            logger.debug(id + " is a directory. Doing a recursive delete");
            session.delete(session.queryBuilder().queryString("path:" + id + "*").toQuery());
            session.delete(RepositoryEntry.class, id);
          } else {
            // Single entry delete
            session.delete(RepositoryEntry.class, id);
          }
        }

        // Apply adds...
        for (RepositoryEntry entry : entriesToAdd) {
          session.save(entry);
        }

      }
    });
  }

  /**
   * {@inheritDoc}
   */
  public void removeDirectory(final String pathAndName) {
    final CompassTemplate template = new CompassTemplate(compass);
    template.execute(new CompassCallbackWithoutResult() {
      protected void doInCompassWithoutResult(final CompassSession session) {
        session.delete(session.queryBuilder().queryString("path:" + pathAndName + "*").toQuery());
        session.delete(RepositoryEntry.class, pathAndName);
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findEntries(final String searchString, final String startDir) {
    final String queryString = "path:" + startDir + "* (name:*" + searchString + "*" +
        " OR lastAuthor:*" + searchString + "*)";

    if (logger.isDebugEnabled()) {
      logger.debug("Finding string [" + searchString + "] starting in [" + startDir + "]");
      logger.debug("QueryString: " + queryString);
    }

    final CompassTemplate template = new CompassTemplate(compass);
    final List<RepositoryEntry> result = toEntriesList(template.findWithDetach(queryString));
    logResult(result);
    return result;
  }

  public List<RepositoryEntry> findEntriesByCamelCasePattern(final CamelCasePattern camelCasePattern,
                                                             final String startPath) {
    final String queryString = "path:" + startPath +
        "* camelCasePattern:" + camelCasePattern.toString().toLowerCase() + "*";

    if (logger.isDebugEnabled()) {
      logger.debug("Finding pattern [" + camelCasePattern + "] starting in [" + startPath + "]");
      logger.debug("QueryString: " + queryString);
    }

    final CompassTemplate template = new CompassTemplate(compass);
    final List<RepositoryEntry> result = toEntriesList(template.findWithDetach(queryString));
    logResult(result);
    return result;
  }

  /**
   * {@inheritDoc}
   */
  public List<RepositoryEntry> findDirectories(final String startPath) {
    final String queryString = "path:" + startPath + "* kind:DIR";

    if (logger.isDebugEnabled()) {
      logger.debug("Finding directories recursively from [" + startPath + "]");
      logger.debug("QueryString: " + queryString);
    }

    final CompassTemplate template = new CompassTemplate(compass);
    final List<RepositoryEntry> result = toEntriesList(template.findWithDetach(queryString));
    logResult(result);
    return result;
  }

  protected List<RepositoryEntry> toEntriesList(final CompassDetachedHits compassHits) {
    final List<RepositoryEntry> hits = new ArrayList<RepositoryEntry>(compassHits.length());
    for (CompassHit compassHit : compassHits) {
      hits.add((RepositoryEntry) compassHit.getData());
    }
    return hits;
  }

  protected void logResult(final List<RepositoryEntry> result) {
    if (logger.isDebugEnabled()) {
      logger.debug("Result count: " + result.size());
      logger.debug("Result: " + result);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void flush() throws CacheException {
    if (useDiskStore) {
      saveLatestRevisionNumber();
    }
  }

  /**
   * @throws CacheException if unable to save revision number.
   */
  private void saveLatestRevisionNumber() throws CacheException {
    logger.info("Saving file to disk, " + latestCachedRevisionFile);
    ObjectOutputStream out = null;
    try {
      out = new ObjectOutputStream(new FileOutputStream(latestCachedRevisionFile));
      out.writeLong(getLatestCachedRevisionNumber());
      out.flush();
      out.close();
    } catch (IOException ioex) {
      throw new CacheException("Unable to store file to disk", ioex);
    } finally {
      IOUtils.closeQuietly(out);
    }
  }


  /**
   * Loads the latest cached revision number from disk.
   *
   * @throws CacheException if unable to read file.
   */
  private void loadLatestCachedRevisionNumber() throws CacheException {
    logger.info("Loading file from disk, " + latestCachedRevisionFile);
    ObjectInputStream inputStream = null;
    if (latestCachedRevisionFile.exists()) {
      try {
        inputStream = new ObjectInputStream(new FileInputStream(latestCachedRevisionFile));
        setLatestCachedRevisionNumber(inputStream.readLong());
        logger.debug("Revision: " + getLatestCachedRevisionNumber());
      } catch (IOException ex) {
        throw new CacheException("Unable to read file from disk", ex);
      } finally {
        IOUtils.closeQuietly(inputStream);
      }
    }
  }
}
