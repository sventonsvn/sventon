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
package org.sventon.cache.logmessagecache;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.compass.core.*;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassEnvironment;
import org.sventon.model.LogMessageSearchItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for caching/indexing of log messages.
 *
 * @author jesper@sventon.org
 * @link http://www.compass-project.org/
 */
public final class CompassLogMessageCache implements LogMessageCache {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  private final File cacheDirectory;

  private Compass compass;

  private final CompassConfiguration compassConfiguration = new CompassConfiguration();

  private final boolean useDiskStore;

  /**
   * Constructs an in-memory cache instance.
   *
   * @param cacheRootDirectory Cache root directory
   */
  public CompassLogMessageCache(final File cacheRootDirectory) {
    this(cacheRootDirectory, false);
  }

  /**
   * Constructor.
   *
   * @param cacheDirectory Cache directory
   * @param useDiskStore   If true index will be stored to disk. Otherwise it will be kept in memory.
   */
  public CompassLogMessageCache(final File cacheDirectory, final boolean useDiskStore) {
    this.cacheDirectory = cacheDirectory;
    this.useDiskStore = useDiskStore;
  }

  @Override
  public void init() {
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
        .addClass(LogMessageSearchItem.class);
    compass = compassConfiguration.buildCompass();
  }

  @Override
  public List<LogMessageSearchItem> find(final String queryString) {
    if (logger.isDebugEnabled()) {
      logger.debug("Finding [" + queryString + "]");
    }

    final CompassTemplate template = new CompassTemplate(compass);
    return template.execute(new CompassCallback<List<LogMessageSearchItem>>() {
      public List<LogMessageSearchItem> doInCompass(CompassSession session) {
        final CompassHits compassHits = session.find(queryString);
        final List<LogMessageSearchItem> hits = new ArrayList<LogMessageSearchItem>(compassHits.length());
        for (int i = 0; i < compassHits.length(); i++) {
          final LogMessageSearchItem logEntry = (LogMessageSearchItem) compassHits.hit(i).getData();
          compassHits.highlighter(i).fragment("message");
          compassHits.highlighter(i).fragment("author");
          final String highLightedMessage = compassHits.hit(i).getHighlightedText().getHighlightedText("message");
          final String highLightedAuthor = compassHits.hit(i).getHighlightedText().getHighlightedText("author");
          if (isAvailable(highLightedAuthor)) {
            logEntry.setAuthor(highLightedAuthor);
          }
          if (isAvailable(highLightedMessage)) {
            logEntry.setMessage(highLightedMessage);
          }
          hits.add(logEntry);
        }
        return hits;
      }
    });
  }

  private boolean isAvailable(String text) {
    return StringUtils.isNotBlank(text) && !text.equals(LogMessageSearchItem.NOT_AVAILABLE_TAG);
  }

  @Override
  public List<LogMessageSearchItem> find(final String queryString, final String startDir) {
    return find("message:" + queryString + " paths:" + startDir + "*");
  }

  @Override
  public void add(final LogMessageSearchItem... logEntries) {
    final CompassTemplate template = new CompassTemplate(compass);
    template.execute(new CompassCallbackWithoutResult() {
      protected void doInCompassWithoutResult(CompassSession session) {
        for (LogMessageSearchItem logEntry : logEntries) {
          session.save(logEntry);
        }
      }
    });
  }

  @Override
  public int getSize() {
    final CompassTemplate template = new CompassTemplate(compass);
    return template.execute(new CompassCallback<Integer>() {
      public Integer doInCompass(CompassSession session) {
        final CompassHits compassHits = session.queryBuilder().matchAll().hits();
        return compassHits.length();
      }
    });
  }

  @Override
  public void clear() {
    final CompassTemplate template = new CompassTemplate(compass);
    template.execute(new CompassCallbackWithoutResult() {
      protected void doInCompassWithoutResult(CompassSession session) {
        session.delete(session.queryBuilder().matchAll());
      }
    });
  }

  @Override
  public void shutdown() {
    compass.close();
  }

}
