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
package de.berlios.sventon.rss;

import de.berlios.sventon.appl.RepositoryConfiguration;
import org.tmatesoft.svn.core.SVNLogEntry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Interface to be implemented by feed generators.
 *
 * @author jesper@users.berlios.de
 */
public interface RssFeedGenerator {

  /**
   * Outputs the generated feed to given writer.
   *
   * @param repositoryConfiguration Repository configuration.
   * @param logEntries              The log entries.
   * @param request                 Servlet request.
   * @param response                Servlet response.
   * @throws Exception if unable to output feed.
   */
  void outputFeed(final RepositoryConfiguration repositoryConfiguration,
                  final List<SVNLogEntry> logEntries,
                  final HttpServletRequest request,
                  final HttpServletResponse response) throws Exception;
}
