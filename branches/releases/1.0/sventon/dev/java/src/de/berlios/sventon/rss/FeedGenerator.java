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
package de.berlios.sventon.rss;

import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.List;
import java.io.Writer;

/**
 * Interface to be implemented by feed generators
 *
 * @author jesper@users.berlios.de
 */
public interface FeedGenerator {

  /**
   * Generates a feed based on given log entries.
   *
   * @param logEntries The log entries
   * @param baseURL    Base URL used to build anchor links. Must end with a slash (/).
   */
  void generateFeed(final List<SVNLogEntry> logEntries, final String baseURL);

  /**
   * Outputs the generated feed to given writer.
   *
   * @param writer Writer.
   * @throws Exception if unable to output feed.
   */
  void outputFeed(final Writer writer) throws Exception;

}
