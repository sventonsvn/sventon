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
package org.sventon.cache.logmessagecache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.compass.core.*;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassEnvironment;
import org.sventon.cache.CacheException;
import org.sventon.model.LogMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains cached log messages.
 * This implementation uses <a href="http://lucene.apache.org">Lucene</a> internally.
 *
 * @author jesper@sventon.org
 */
public final class LogMessageCacheImpl implements LogMessageCache {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  private final File cacheDirectory;

  private Compass compass;

  private final CompassConfiguration compassConfiguration = new CompassConfiguration();

  private boolean useDiskStore;

  /**
   * Constructs an in-memory cache instance.
   *
   * @param cacheRootDirectory Cache root directory
   */
  public LogMessageCacheImpl(final File cacheRootDirectory) {
    this(cacheRootDirectory, false);
  }

  /**
   * Constructor.
   *
   * @param cacheDirectory Cache directory
   * @param useDiskStore   If true index will be stored to disk. Otherwise it will be kept in memory.
   */
  public LogMessageCacheImpl(final File cacheDirectory, final boolean useDiskStore) {
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
        .setSetting("compass.engine.highlighter.default.encoder.type", "html")
        .setSetting("compass.engine.highlighter.default.fragmenter.type", "null")
        .setSetting("compass.engine.highlighter.default.formatter.type", "simple")
        .setSetting("compass.engine.highlighter.default.formatter.simple.pre", "<span class=\"searchhit\">")
        .setSetting("compass.engine.highlighter.default.formatter.simple.post", "</span>")
        .setSetting("compass.engine.queryParser.default.allowConstantScorePrefixQuery", "false")
        .addClass(LogMessage.class);
    compass = compassConfiguration.buildCompass();
  }

  /**
   * {@inheritDoc}
   */
  public List<LogMessage> find(final String queryString) throws CacheException {
    if (logger.isDebugEnabled()) {
      logger.debug("Finding [" + queryString + "]");
    }

    final CompassTemplate template = new CompassTemplate(compass);
    return template.execute(new CompassCallback<List<LogMessage>>() {
      public List<LogMessage> doInCompass(CompassSession session) throws CompassException {
        final CompassHits compassHits = session.find("message:" + queryString);
        final List<LogMessage> hits = new ArrayList<LogMessage>(compassHits.length());
        for (int i = 0; i < compassHits.length(); i++) {
          compassHits.highlighter(i).fragment("message");
          final LogMessage logMessage = (LogMessage) compassHits.hit(i).getData();
          logMessage.setMessage(compassHits.hit(i).getHighlightedText().getHighlightedText());
          hits.add(logMessage);
        }
        return hits;
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  public void add(final LogMessage... logMessages) throws CacheException {
    final CompassTemplate template = new CompassTemplate(compass);
    template.execute(new CompassCallbackWithoutResult() {
      protected void doInCompassWithoutResult(CompassSession session) throws CompassException {
        for (LogMessage logMessage : logMessages) {
          session.save(logMessage);
        }
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  public int getSize() throws CacheException {
    final CompassTemplate template = new CompassTemplate(compass);
    return template.execute(new CompassCallback<Integer>() {
      public Integer doInCompass(CompassSession session) throws CompassException {
        final CompassHits compassHits = session.queryBuilder().matchAll().hits();
        return compassHits.length();
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  public void clear() throws CacheException {
    final CompassTemplate template = new CompassTemplate(compass);
    template.execute(new CompassCallbackWithoutResult() {
      protected void doInCompassWithoutResult(CompassSession session) throws CompassException {
        session.delete(session.queryBuilder().matchAll());
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  public void shutdown() throws CacheException {
    compass.close();
  }

}
