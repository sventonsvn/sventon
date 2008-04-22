/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.repository.cache.logmessagecache;

import de.berlios.sventon.appl.RepositoryName;
import de.berlios.sventon.repository.cache.CacheException;
import de.berlios.sventon.repository.cache.CacheManager;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

/**
 * Handles LogMessageCache instances.
 *
 * @author jesper@users.berlios.de
 */
public final class LogMessageCacheManager extends CacheManager<LogMessageCache> {

  /**
   * Directory where to store cache files.
   */
  private final File rootDirectory;

  /**
   * Lucene Analyzer to use.
   *
   * @see org.apache.lucene.analysis.Analyzer
   */
  private final String analyzerClassName;

  /**
   * Constructor.
   *
   * @param rootDirectory     Root directory to use.
   * @param analyzerClassName Analyzer to use.
   */
  public LogMessageCacheManager(final File rootDirectory, final String analyzerClassName) {
    logger.debug("Starting cache manager. Using [" + rootDirectory + "] as root directory");
    this.rootDirectory = rootDirectory;
    this.analyzerClassName = analyzerClassName;
    System.setProperty("org.apache.lucene.lockDir", rootDirectory.getAbsolutePath());
  }

  /**
   * Creates a new cache instance using given name and default settings.
   *
   * @param repositoryName Name of cache instance.
   * @return The created cache instance.
   * @throws CacheException if unable to create cache or unable to load analyzer.
   */
  protected LogMessageCache createCache(final RepositoryName repositoryName) throws CacheException {
    logger.debug("Creating cache: " + repositoryName);
    final FSDirectory fsDirectory;
    try {
      final File cachePath = new File(new File(rootDirectory, repositoryName.toString()), "cache");
      cachePath.mkdirs();
      fsDirectory = FSDirectory.getDirectory(cachePath);
      logger.debug("Log cache dir: " + fsDirectory.getFile().getAbsolutePath());
    } catch (IOException ioex) {
      throw new CacheException("Unable to create LogMessageCache instance", ioex);
    }

    final Class<?> analyzer;
    try {
      logger.debug("Loading analyzer [" + analyzerClassName + "]");
      analyzer = Class.forName(analyzerClassName);
    } catch (final ClassNotFoundException cnfe) {
      throw new CacheException("Unable to load analyzer [" + analyzerClassName + "]", cnfe);
    }
    //noinspection unchecked
    return new LogMessageCacheImpl(fsDirectory, (Class) analyzer);
  }

}
