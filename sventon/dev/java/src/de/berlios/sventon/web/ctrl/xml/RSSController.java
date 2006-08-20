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
package de.berlios.sventon.web.ctrl.xml;

import de.berlios.sventon.config.ApplicationConfiguration;
import de.berlios.sventon.repository.RepositoryFactory;
import de.berlios.sventon.rss.FeedGenerator;
import de.berlios.sventon.service.RepositoryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.bind.RequestUtils;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
   * RSS mime type, default set to <tt>application/xml; charset=UTF-8</tt>.
   */
  private String mimeType = "application/xml; charset=UTF-8";

  /**
   * Number of items in the feed, default set to 10.
   */
  private int feedItemCount = 10;

  /**
   * The application configuration.
   */
  private ApplicationConfiguration configuration;

  /**
   * The feed generator.
   */
  private FeedGenerator feedGenerator;

  /**
   * The repository service instance.
   */
  private RepositoryService repositoryService;

  /**
   * {@inheritDoc}
   */
  protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response)
      throws Exception {

    logger.debug("Getting RSS feed");
    response.setContentType(mimeType);
    response.setHeader("Cache-Control", "no-cache");

    final String instanceName = RequestUtils.getStringParameter(request, "name", null);

    if (instanceName == null) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No 'name' parameter provided.");
      return null;
    }

    final SVNRepository repository =
        RepositoryFactory.INSTANCE.getRepository(configuration.getInstanceConfiguration(instanceName));

    if (repository == null) {
      String errorMessage = "Unable to connect to repository!";
      logger.error(errorMessage + " Have sventon been configured?");
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
      return null;
    }

    try {
      final long headRevision = repository.getLatestRevision();
      logger.debug("Producing feed for revision: " + headRevision);

      final List<SVNLogEntry> logEntries = repositoryService.getRevisions(repository, headRevision,
          headRevision - feedItemCount, feedItemCount);
      logger.debug("Outputting feed");
      feedGenerator.outputFeed(instanceName, logEntries, getRequestURL(request), response.getWriter());
    } catch (Exception ex) {
      final String errorMessage = "Unable to generate RSS feed";
      logger.warn(errorMessage, ex);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
    }
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

  /**
   * Set application configuration.
   *
   * @param configuration ApplicationConfiguration
   */
  public void setConfiguration(final ApplicationConfiguration configuration) {
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

  /**
   * Sets the repository service instance.
   *
   * @param repositoryService The service instance.
   */
  public void setRepositoryService(final RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

}
