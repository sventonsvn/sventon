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
package de.berlios.sventon.web.ctrl.xml;

import de.berlios.sventon.appl.Application;
import de.berlios.sventon.appl.InstanceConfiguration;
import de.berlios.sventon.repository.RepositoryFactory;
import de.berlios.sventon.rss.FeedGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
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
public final class RSSController extends AbstractController {

  /**
   * Logger for this class and subclasses.
   */
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * RSS mime type, default set to <tt>application/xml; charset=UTF-8</tt>.
   */
  private String mimeType = "application/xml; charset=UTF-8";

  /**
   * The application.
   */
  private Application application;

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
    response.setHeader("Cache-Control", "no-cache");

    final String instanceName = ServletRequestUtils.getRequiredStringParameter(request, "name");
    final String path = ServletRequestUtils.getStringParameter(request, "path", "/");

    if (!application.isConfigured()) {
      String errorMessage = "Unable to connect to repository!";
      logger.error(errorMessage + " Have sventon been configured?");
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
      return null;
    }

    final InstanceConfiguration configuration = application.getInstance(instanceName).getConfiguration();
    final SVNRepository repository = RepositoryFactory.INSTANCE.getRepository(configuration.getSVNURL(),
        configuration.getUid(), configuration.getPwd());

    try {
      logger.debug("Outputting feed for [" + path + "]");
      final List<SVNLogEntry> logEntries = application.getRepositoryService().getLatestRevisions(
          instanceName, path, repository, configuration.getRssItemsCount());
      feedGenerator.outputFeed(instanceName, logEntries, request, response);
    } catch (Exception ex) {
      final String errorMessage = "Unable to generate RSS feed";
      logger.warn(errorMessage, ex);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
    }
    return null;
  }

  /**
   * Sets the application.
   *
   * @param application Application
   */
  public void setApplication(final Application application) {
    this.application = application;
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

}
