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

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import de.berlios.sventon.rss.FeedGenerator;
import de.berlios.sventon.svnsupport.RepositoryFactory;
import de.berlios.sventon.ctrl.RepositoryConfiguration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

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
   * The generated feed type, default set to <tt>rss_2.0</tt>.
   */
  private String feedType = "rss_2.0";

  /**
   * RSS mime type, default set to <tt>application/xml; charset=UTF-8</tt>.
   */
  private String feedMimeType = "application/xml; charset=UTF-8";

  /**
   * Number of items in the feed, default set to 10.
   */
  private int feedItemCount = 10;

  /**
   * The repository configuration.
   */
  private RepositoryConfiguration configuration;

  /**
   * The cached feed.
   */
  private SyndFeed cachedFeed;

  /**
   * The cached feed head revision.
   */
  private long cachedFeedHeadRevision;

  /**
   * {@inheritDoc}
   */
  protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
    logger.debug("Getting RSS feed");
    response.setContentType(feedMimeType);
    SyndFeedOutput output = new SyndFeedOutput();

    SVNRepository repository = RepositoryFactory.INSTANCE.getRepository(configuration);
    if (repository == null) {
      String errorMessage = "Unable to connect to repository!";
      logger.error(errorMessage + " Have sventon been configured?");
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
      return null;
    }

    final long headRevision = repository.getLatestRevision();
    logger.debug("Latest revision is: " + headRevision);

    if (cachedFeedHeadRevision == headRevision) {
      logger.debug("Returning cached feed");
      output.output(cachedFeed, response.getWriter());
      return null;
    }

    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    final String[] targetPaths = new String[]{"/"}; // the path to show logs for

    logger.debug("Getting log info for latest " + feedItemCount + " revisions");
    repository.log(targetPaths, headRevision, headRevision - feedItemCount, true, false, feedItemCount, new ISVNLogEntryHandler() {
      public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
        logEntries.add(logEntry);
      }
    });

    SyndFeed feed;

    try {
      feed = new FeedGenerator().generateFeed(logEntries, getRequestURL(request));
      feed.setFeedType(feedType);
      logger.debug("Outputting feed");
      output.output(feed, response.getWriter());
    }
    catch (FeedException ex) {
      String errorMessage = "Unable to generate RSS feed";
      logger.warn(errorMessage, ex);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
      return null;
    }

    logger.debug("Caching feed");
    cachedFeed = feed;
    cachedFeedHeadRevision = headRevision;
    return null;
  }

  private String getRequestURL(HttpServletRequest request) {
    StringBuilder sb = new StringBuilder();
    sb.append(request.getScheme());
    sb.append("://");
    sb.append(request.getServerName());
    sb.append(":");
    sb.append(request.getServerPort());
    sb.append(request.getContextPath());
    sb.append("/");
    return sb.toString();
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
   * Get current repository configuration.
   *
   * @return Configuration
   */
  public RepositoryConfiguration getRepositoryConfiguration() {
    return configuration;
  }

  /**
   * Sets the feed type. For available types check ROME documentation.
   *
   * @param feedType
   * @link https://rome.dev.java.net/
   */
  public void setFeedType(final String feedType) {
    this.feedType = feedType;
  }

  /**
   * Gets the feed type. For available types check ROME documentation.
   *
   * @return The feed type.
   * @link https://rome.dev.java.net/
   */
  public String getFeedType() {
    return feedType;
  }

  /**
   * Gets the number of items to be included in the feed.
   *
   * @return Item count
   */
  public int getFeedItemCount() {
    return feedItemCount;
  }

  /**
   * Sets the number of items to be included in the feed.
   *
   * @param feedItemCount The number of items (revisions).
   */
  public void setFeedItemCount(int feedItemCount) {
    this.feedItemCount = feedItemCount;
  }

  /**
   * Gets the mime-type for the feed.
   *
   * @return The mime-type.
   */
  public String getFeedMimeType() {
    return feedMimeType;
  }

  /**
   * Sets the mime-type for the feed.
   *
   * @param feedMimeType The mime-type
   */
  public void setFeedMimeType(String feedMimeType) {
    this.feedMimeType = feedMimeType;
  }

}
