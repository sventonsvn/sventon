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
package de.berlios.sventon.repository.cache.logmessagecache;

import de.berlios.sventon.repository.LogMessage;
import de.berlios.sventon.repository.cache.CacheException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains cached log messages.
 * This implementation uses <a href="http://lucene.apache.org">Lucene</a> internally.
 *
 * @author jesper@users.berlios.de
 */
public class LogMessageCacheImpl implements LogMessageCache {

  /**
   * The logging instance.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * The <i>lucene</i> directory.
   */
  private Directory directory;

  /**
   * Constructor.
   * Initializes the log message cache.
   *
   * @param directory The <i>lucene</i> directory.
   */
  public LogMessageCacheImpl(final Directory directory) throws CacheException {
    logger.debug("Initializing cache");

    IndexWriter writer = null;
    try {
      if (!IndexReader.indexExists(directory)) {
        writer = new IndexWriter(directory, new StandardAnalyzer(), true);
        writer.close();
      }
      this.directory = directory;
    } catch (IOException ioex) {
      throw new CacheException("Unable to startup lucene index", ioex);
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException ioex) {
          throw new CacheException("Unable to startup lucene index", ioex);
        }
      }
    }
  }

  /**
   * Gets the index Searcher.
   *
   * @throws IOException          if unable to create searcher.
   * @throws NullPointerException if index does not exist.
   */
  private synchronized Searcher getIndexSearcher() throws IOException {
    return new IndexSearcher(directory);
  }

  /**
   * {@inheritDoc}
   */
  public synchronized List<LogMessage> find(final String queryString) throws CacheException {
    final List<LogMessage> result = new ArrayList<LogMessage>();
    Searcher searcher = null;
    try {
      logger.debug("Searching for: [" + queryString + "]");
      final Query query = new QueryParser("content", new StandardAnalyzer()).parse(queryString);
      searcher = getIndexSearcher();
      final Hits hits = searcher.search(query);

      final SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<span class=\"searchHit\">", "</span>");
      final Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));
      highlighter.setTextFragmenter(new NullFragmenter());
      highlighter.setEncoder(new SimpleHTMLEncoder());

      final int hitCount = hits.length();
      logger.debug("Hit count: " + hitCount);

      if (hitCount > 0) {
        for (int i = 0; i < hitCount; i++) {
          final Document document = hits.doc(i);
          final String content = document.get("content");
          final TokenStream tokenStream = new StandardAnalyzer().tokenStream("content", new StringReader(content));
          final String highlightedContent = highlighter.getBestFragment(tokenStream, content);
          result.add(new LogMessage(Long.parseLong(document.get("revision")), highlightedContent));
        }
      }
    } catch (Exception ex) {
      throw new CacheException("Unable to perform lucene search", ex);
    } finally {
      if (searcher != null) {
        try {
          searcher.close();
        } catch (IOException ioex) {
          throw new CacheException("Unable to close lucene searcher", ioex);
        }
      }
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  public synchronized void add(final LogMessage logMessage) throws CacheException {
    IndexWriter writer = null;

    try {
      writer = new IndexWriter(directory, new StandardAnalyzer(), false);
      final Document document = new Document();
      document.add(new Field("revision", String.valueOf(logMessage.getRevision()), Field.Store.YES, Field.Index.NO));
      document.add(new Field("content", logMessage.getMessage() == null ? "" :
          logMessage.getMessage(), Field.Store.YES, Field.Index.TOKENIZED));
      writer.addDocument(document);
    } catch (IOException ioex) {
      throw new CacheException("Unable to add content to lucene cache", ioex);
    } finally {
      if (writer != null) {
        // Optimize and close the writer to finish building the index
        try {
          writer.optimize();
        } catch (IOException ioex) {
          throw new CacheException("Unable to optimize lucene index", ioex);
        } finally {
          try {
            writer.close();
          } catch (IOException ioex) {
            throw new CacheException("Unable to close lucene index", ioex);
          }
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public int getSize() throws CacheException {
    int count = -1;
    IndexWriter writer = null;

    try {
      writer = new IndexWriter(directory, new StandardAnalyzer(), false);
      count = writer.docCount();
      writer.close();
    } catch (IOException ioex) {
      throw new CacheException("Unable to get lucene cache size", ioex);
    } finally {
      if (writer != null) {
        // Close the writer
        try {
          writer.close();
        } catch (IOException ioex) {
          throw new CacheException("Unable to close lucene index", ioex);
        }
      }
    }
    return count;
  }

}
