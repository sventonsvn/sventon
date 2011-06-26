/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.cache.direntrycache;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.queryParser.QueryParser;
import org.compass.core.*;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.lucene.LuceneEnvironment;
import org.sventon.cache.CacheException;
import org.sventon.model.CamelCasePattern;
import org.sventon.model.DirEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.sventon.model.DirEntry.Kind;

/**
 * CompassDirEntryCache.
 */
public final class CompassDirEntryCache implements DirEntryCache {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Default filename for storing the latest cached revision number.
   */
  private static final String ENTRY_CACHE_FILENAME = "direntrycache.ser";

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

  private static final String PROPERTY_KEY_MAX_CLAUSE_COUNT = "sventon.maxClauseCount";

  private static final int MAX_CLAUSE_DEFAULT_COUNT = 1024;

  /**
   * Constructs an in-memory cache instance.
   *
   * @param cacheRootDirectory Cache root directory
   */
  public CompassDirEntryCache(final File cacheRootDirectory) {
    this(cacheRootDirectory, false);
  }

  /**
   * Constructor.
   *
   * @param cacheDirectory Cache directory
   * @param useDiskStore   If true index will be stored to disk. Otherwise it will be kept in memory.
   */
  public CompassDirEntryCache(final File cacheDirectory, final boolean useDiskStore) {
    this.cacheDirectory = cacheDirectory;
    this.useDiskStore = useDiskStore;
  }

  @Override
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
        .setSetting(LuceneEnvironment.Query.MAX_CLAUSE_COUNT, System.getProperty(PROPERTY_KEY_MAX_CLAUSE_COUNT,
            Integer.toString(MAX_CLAUSE_DEFAULT_COUNT)))
        .addClass(DirEntry.class);
    compass = compassConfiguration.buildCompass();
    latestCachedRevisionFile = new File(cacheDirectory, ENTRY_CACHE_FILENAME);

    if (useDiskStore) {
      loadLatestCachedRevisionNumber();
    }
  }

  @Override
  public void shutdown() {
    compass.close();
  }

  @Override
  public void setLatestCachedRevisionNumber(final long revision) {
    this.cachedRevision.set(revision);
  }

  @Override
  public long getLatestCachedRevisionNumber() {
    return cachedRevision.get();
  }

  @Override
  public int getSize() {
    final CompassTemplate template = new CompassTemplate(compass);
    return template.execute(new CompassCallback<Integer>() {
      public Integer doInCompass(final CompassSession session) {
        final CompassHits compassHits = session.queryBuilder().matchAll().hits();
        return compassHits.length();
      }
    });
  }

  @Override
  public void add(final DirEntry... entries) {
    final CompassTemplate template = new CompassTemplate(compass);
    template.execute(new CompassCallbackWithoutResult() {
      protected void doInCompassWithoutResult(CompassSession session) {
        for (DirEntry entry : entries) {
          session.save(entry);
        }
      }
    });
  }

  @Override
  public void clear() {
    final CompassTemplate template = new CompassTemplate(compass);
    template.execute(new CompassCallbackWithoutResult() {
      protected void doInCompassWithoutResult(final CompassSession session) {
        session.delete(session.queryBuilder().matchAll());
      }
    });
  }

  @Override
  public void removeFile(final String pathAndName) {
    final CompassTemplate template = new CompassTemplate(compass);
    template.execute(new CompassCallbackWithoutResult() {
      protected void doInCompassWithoutResult(CompassSession session) {
        removeFileEntry(session, pathAndName);
      }
    });
  }

  @Override
  public void update(final Map<String, DirEntry.Kind> entriesToDelete,
                     final List<DirEntry> entriesToAdd) {

    final CompassTemplate template = new CompassTemplate(compass);
    template.execute(new CompassCallbackWithoutResult() {
      protected void doInCompassWithoutResult(final CompassSession session) {
        logger.debug("Applying changes inside transaction...");

        // Apply deletion...
        for (String id : entriesToDelete.keySet()) {
          final DirEntry.Kind kind = entriesToDelete.get(id);
          if (kind == Kind.DIR) {
            // Directory node deleted
            logger.debug(id + " is a directory. Doing a recursive delete");
            removeAllEntriesInDirectory(session, id);
          } else {
            // Single entry delete
            removeFileEntry(session, id);
          }
        }

        // Apply adds...
        for (DirEntry entry : entriesToAdd) {
          session.save(entry);
        }

      }
    });
  }

  @Override
  public void removeDirectory(final String pathAndName) {
    final CompassTemplate template = new CompassTemplate(compass);
    template.execute(new CompassCallbackWithoutResult() {
      protected void doInCompassWithoutResult(final CompassSession session) {
        removeAllEntriesInDirectory(session, pathAndName);
      }
    });
  }

  @Override
  public List<DirEntry> findEntries(final String searchString, final String startDir) {
    final String escapedSearchString = QueryParser.escape(searchString);
    final String queryString = "path:" + startDir + "* (name:" + escapedSearchString +
        " OR lastAuthor:" + escapedSearchString + " OR nameFragments:" + escapedSearchString + ")";

    if (logger.isDebugEnabled()) {
      logger.debug("Finding string [" + escapedSearchString + "] starting in [" + startDir + "]");
      logger.debug("QueryString: " + queryString);
    }

    final CompassTemplate template = new CompassTemplate(compass);
    final List<DirEntry> result = toEntriesList(template.findWithDetach(queryString));
    logResult(result);
    return result;
  }

  @Override
  public List<DirEntry> findEntriesByCamelCasePattern(final CamelCasePattern camelCasePattern,
                                                      final String startPath) {

    final String pattern = camelCasePattern.toString().toLowerCase();
    final String queryString = "path:" + startPath + "* camelCasePattern:" + pattern + "*";

    if (logger.isDebugEnabled()) {
      logger.debug("Finding pattern [" + camelCasePattern + "] starting in [" + startPath + "]");
      logger.debug("QueryString: " + queryString);
    }

    final CompassTemplate template = new CompassTemplate(compass);
    final List<DirEntry> result = toEntriesList(template.findWithDetach(queryString));
    logResult(result);
    return result;
  }

  @Override
  public List<DirEntry> findDirectories(final String startPath) {
    final String queryString = "path:" + startPath + "* kind:DIR";

    if (logger.isDebugEnabled()) {
      logger.debug("Finding directories recursively from [" + startPath + "]");
      logger.debug("QueryString: " + queryString);
    }

    final CompassTemplate template = new CompassTemplate(compass);
    final List<DirEntry> result = toEntriesList(template.findWithDetach(queryString));
    logResult(result);
    return result;
  }

  protected List<DirEntry> toEntriesList(final CompassDetachedHits compassHits) {
    final List<DirEntry> hits = new ArrayList<DirEntry>(compassHits.length());
    for (CompassHit compassHit : compassHits) {
      hits.add((DirEntry) compassHit.getData());
    }
    return hits;
  }

  protected void logResult(final List<DirEntry> result) {
    if (logger.isDebugEnabled()) {
      logger.debug("Result count: " + result.size());
      logger.debug("Result: " + result);
    }
  }

  @Override
  public void flush() throws CacheException {
    if (useDiskStore) {
      saveLatestRevisionNumber();
    }
  }

  private void removeFileEntry(CompassSession session, String pathAndName) {
    session.delete(DirEntry.class, pathAndName + Kind.FILE);
  }

  private void removeAllEntriesInDirectory(final CompassSession session, final String pathAndName) {
    session.delete(session.queryBuilder().queryString("path:" + pathAndName + "*").toQuery());
    session.delete(DirEntry.class, pathAndName + Kind.DIR);
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
