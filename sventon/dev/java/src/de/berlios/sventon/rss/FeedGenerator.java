/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
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
 * Interface to be implemented by feed generators.
 *
 * @author jesper@users.berlios.de
 */
public interface FeedGenerator {

  /**
   * Outputs the generated feed to given writer.
   *
   * @param instanceName The instance name.
   * @param logEntries   The log entries.
   * @param baseURL      Base URL used to build anchor links. Must end with a slash (/).
   * @param writer       Writer.
   * @throws Exception if unable to output feed.
   */
  void outputFeed(final String instanceName,
                  final List<SVNLogEntry> logEntries,
                  final String baseURL,
                  final Writer writer) throws Exception;
}
