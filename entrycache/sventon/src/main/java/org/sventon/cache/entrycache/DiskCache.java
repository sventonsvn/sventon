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
package org.sventon.cache.entrycache;

import org.apache.commons.io.IOUtils;
import org.sventon.cache.CacheException;
import org.sventon.model.RepositoryEntry;
import org.sventon.util.RepositoryEntryComparator;

import java.io.*;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Disk persistend repository entry cache.
 * Contains a cached set of the repository entries for a specific revision and URL.
 *
 * @author jesper@sventon.org
 */
public final class DiskCache extends EntryCache {

  /**
   * Default cache filename.
   */
  private static final String ENTRY_CACHE_FILENAME = "entrycache.ser";

  /**
   * The cache file.
   */
  private File cacheFile;

  private File cacheDirectory;

  /**
   * Constructor.
   * Loads the persisted cache from disk, if it exists.
   * If not, a new empty cache will be created.
   *
   * @param cacheDirectory The path where the cache file is located.
   * @throws CacheException if unable to load cache file.
   */
  public DiskCache(final File cacheDirectory) throws CacheException {
    this.cacheDirectory = cacheDirectory;
  }

  /**
   * {@inheritDoc}
   */
  public void init() throws CacheException {
    logger.info("Initializing DiskCache");
    logger.debug("Using directory: " + cacheDirectory.getAbsolutePath());
    cacheDirectory.mkdirs();
    cacheFile = new File(cacheDirectory, ENTRY_CACHE_FILENAME);
    load();
  }

  /**
   * Loads the disk persisted entry cache.
   *
   * @throws CacheException if unable to read cache file.
   */
  private void load() throws CacheException {
    ObjectInputStream inputStream = null;
    if (cacheFile.exists()) {
      try {
        inputStream = new ObjectInputStream(new GZIPInputStream(new FileInputStream(cacheFile)));
        setCachedRevision(inputStream.readLong());
        //noinspection unchecked
        setEntries((Set<RepositoryEntry>) inputStream.readObject());
        logger.debug("Number of loaded cached entries: " + getSize());
        logger.debug("Revision: " + getCachedRevision());
      } catch (Exception ex) {
        throw new CacheException("Unable to read entryCache file", ex);
      } finally {
        IOUtils.closeQuietly(inputStream);
      }
    } else {
      // No serialized cachefile excisted - initialize an empty one.
      setEntries(Collections.checkedSet(new TreeSet<RepositoryEntry>(
          new RepositoryEntryComparator(RepositoryEntryComparator.SortType.FULL_NAME, false)), RepositoryEntry.class));
    }
  }

  /**
   * {@inheritDoc}
   */
  public synchronized void shutdown() throws CacheException {
    logger.info("Shutting down");
    flush();
  }

  /**
   * {@inheritDoc}
   */
  public void flush() throws CacheException {
    if (getSize() > 0) {
      final File tempCacheFile = new File(cacheFile.getAbsolutePath() + ".tmp");
      logger.info("Saving entryCache to disk, " + tempCacheFile);
      ObjectOutputStream out = null;
      try {
        // Write to a temp file first, to keep the old file just in case.
        out = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(tempCacheFile)));
        out.writeLong(getCachedRevision());
        out.writeObject(getCachedEntries());
        out.flush();
        out.close();
        logger.debug("Deleting old cache file: " + cacheFile.delete());
        logger.info("Renaming tempfile [" + tempCacheFile.getName() + "] to [" + cacheFile.getName() + "] - "
            + tempCacheFile.renameTo(cacheFile));
      } catch (IOException ioex) {
        throw new CacheException("Unable to store entryCache to disk", ioex);
      } finally {
        IOUtils.closeQuietly(out);
      }
    }
  }

}
