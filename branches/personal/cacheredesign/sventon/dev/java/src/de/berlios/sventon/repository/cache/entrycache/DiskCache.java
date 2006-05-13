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
package de.berlios.sventon.repository.cache.entrycache;

import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.repository.RepositoryEntryComparator;
import de.berlios.sventon.repository.cache.CacheException;

import java.io.*;
import java.util.Collections;
import java.util.TreeSet;
import java.util.Set;

/**
 * Disk persistend repository entry cache.
 * Contains a cached set of the repository entries for a specific revision and URL.
 *
 * @author jesper@users.berlios.de
 */
public class DiskCache extends EntryCache {

  /**
   * Default cache filename.
   */
  private static final String ENTRY_CACHE_FILENAME = "sventonEntryCache.ser";

  /**
   * The cache directory and filename.
   */
  private String cacheFileName;

  /**
   * Constructor.
   * Loads the persisted cache from disk, if it exists.
   * If not, a new empty cache will be created.
   *
   * @param cacheDirectoryPath The path where the cache file is located.
   * @throws CacheException if unable to load cache file.
   */
  public DiskCache(final String cacheDirectoryPath) throws CacheException {
    if (cacheDirectoryPath == null || "".equals(cacheDirectoryPath)) {
      return;
    } else {
      logger.info("Initializing DiskCache");
      logger.debug("Using directory: " + cacheDirectoryPath);

      this.cacheFileName = cacheDirectoryPath
          + System.getProperty("file.separator") + ENTRY_CACHE_FILENAME;

      new File(cacheDirectoryPath).mkdirs();
      load();
      initialized = true;
    }
  }

  /**
   * Loads the disk persisted entry cache.
   *
   * @throws CacheException if unable to read cache file.
   */
  private void load() throws CacheException {
    final File entryCacheFile = new File(cacheFileName);
    final ObjectInputStream inputStream;
    if (entryCacheFile.exists()) {
      try {
        inputStream = new ObjectInputStream(new FileInputStream(entryCacheFile));
        setCachedRevision(inputStream.readLong());
        setRepositoryURL((String) inputStream.readObject());
        setEntries((Set<RepositoryEntry>) inputStream.readObject());
        logger.debug("Cached revision is: " + getCachedRevision());
        logger.debug("Number of loaded cached entries: " + getEntries().size());
      } catch (Exception ex) {
        throw new CacheException("Unable to read entryCache file", ex);
      }
    } else {
      // No serialized cachefile exsisted - initialize an empty one.
      setEntries(Collections.checkedSet(new TreeSet<RepositoryEntry>(
          new RepositoryEntryComparator(RepositoryEntryComparator.FULL_NAME, false)),
          RepositoryEntry.class));
    }

  }

  /**
   * {@inheritDoc]
   */
  public synchronized void shutdown() throws CacheException {
    logger.info("Shutting down");
    if (getEntries().size() > 0) {
      logger.info("Saving entryCache to disk, " + cacheFileName);
      final ObjectOutputStream out;
      try {
        out = new ObjectOutputStream(new FileOutputStream(cacheFileName));
        out.writeLong(getCachedRevision());
        out.writeObject(getRepositoryUrl());
        out.writeObject(getEntries());
        out.flush();
        out.close();
      } catch (IOException ioex) {
        throw new CacheException("Unable to store entryCache to disk", ioex);
      }
    }

  }
}
