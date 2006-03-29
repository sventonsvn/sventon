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
package de.berlios.sventon.ctrl.xml;

import de.berlios.sventon.repository.RepositoryConfiguration;
import de.berlios.sventon.repository.RepositoryFactory;
import de.berlios.sventon.rss.FeedGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;

/**
 * Controller used for generating RSS feeds.
 *
 * @author jesper@users.berlios.de
 */
public class RSSController extends AbstractController {

  /**
   * Logger for this class and subclasses.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * RSS mime type, default set to <tt>application/xml; charset=UTF-8</tt>.
   */
  private String mimeType = "application/xml; charset=UTF-8";

  /**
   * Number of items in the feed, default set to 10.
   */
  private int feedItemCount = 10;

  /**
   * The repository configuration.
   */
  private RepositoryConfiguration configuration;

  /**
   * The cached feed head revision.
   */
  private long cachedFeedHeadRevision;

  /**
   * The feed generator.
   */
  private FeedGenerator feedGenerator;

  /**
   * {@inheritDoc}
   */
  protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response)
      throws Exception {

    logger.debug("Getting RSS feed");
    response.setContentType(mimeType);

    final SVNRepository repository = RepositoryFactory.INSTANCE.getRepository(configuration);
    if (repository == null) {
      String errorMessage = "Unable to connect to repository!";
      logger.error(errorMessage + " Have sventon been configured?");
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
      return null;
    }

    try {
      generateFeed(repository, getRequestURL(request));
    } catch (Exception ex) {
      final String errorMessage = "Unable to generate RSS feed";
      logger.warn(errorMessage, ex);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
      return null;
    }

    logger.debug("Outputting feed");
    feedGenerator.outputFeed(response.getWriter());
    return null;
  }

  /**
   * Gets the full request URL, including scheme, server name,
   * server port and context path.
   *
   * @param request The request.
   * @return The full URL.
   */
  private String getRequestURL(final HttpServletRequest request) {
    final StringBuilder sb = new StringBuilder();
    sb.append(request.getScheme());
    sb.append("://");
    sb.append(request.getServerName());
    sb.append(":");
    sb.append(request.getServerPort());
    sb.append(request.getContextPath());
    sb.append("/");
    return sb.toString();
  }

  private synchronized void generateFeed(final SVNRepository repository, final String baseURL) throws Exception {
    final long headRevision = repository.getLatestRevision();
    logger.debug("Cached feed revision is: " + cachedFeedHeadRevision);

    if (cachedFeedHeadRevision != headRevision) {
      logger.debug("Updating feed for revision: " + headRevision);
      final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
      final String[] targetPaths = new String[]{"/"}; // the path to show logs for

      logger.debug("Getting log info for latest " + feedItemCount + " revisions");
      repository.log(targetPaths, headRevision, headRevision - feedItemCount, true, false, feedItemCount, new ISVNLogEntryHandler() {
        public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
          logEntries.add(logEntry);
        }
      });

      feedGenerator.generateFeed(logEntries, baseURL);
      logger.debug("Caching feed revision");
      cachedFeedHeadRevision = headRevision;
    }
  }

  /**
   * Set repository configuration.
   *
   * @param configuration Configuration
   */
  public void setRepositoryConfiguration(final RepositoryConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Sets the mime-type for the feed.
   *
   * @param mimeType The mime-type
   */
  public void setMimeType(final String mimeType) {
    this.mimeType = mimeType;
  }

  /**
   * Sets the feed generator.
   *
   * @param feedGenerator The generator.
   */
  public void setFeedGenerator(final FeedGenerator feedGenerator) {
    this.feedGenerator = feedGenerator;
  }

  /**
   * Sets the number of items to be included in the feed.
   *
   * @param feedItemCount The number of items (revisions).
   */
  public void setFeedItemCount(int feedItemCount) {
    this.feedItemCount = feedItemCount;
  }

}
