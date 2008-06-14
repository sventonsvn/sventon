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
package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.appl.Application;
import de.berlios.sventon.appl.RepositoryConfiguration;
import de.berlios.sventon.appl.RepositoryName;
import de.berlios.sventon.repository.RepositoryFactory;
import de.berlios.sventon.rss.FeedGenerator;
import de.berlios.sventon.service.RepositoryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.tmatesoft.svn.core.SVNAuthenticationException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

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
   * Service.
   */
  private RepositoryService repositoryService;

  private static final String ERROR_MESSAGE = "Unable to generate RSS feed";

  /**
   * The repository factory.
   */
  private RepositoryFactory repositoryFactory;

  /**
   * {@inheritDoc}
   */
  protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response)
      throws Exception {

    logger.debug("Getting RSS feed");
    response.setContentType(mimeType);
    response.setHeader("Cache-Control", "no-cache");

    final RepositoryName repositoryName = new RepositoryName(ServletRequestUtils.getRequiredStringParameter(request, "name"));
    final String path = ServletRequestUtils.getStringParameter(request, "path", "/");
    final String revision = ServletRequestUtils.getStringParameter(request, "revision", "HEAD");
    final String uid = ServletRequestUtils.getStringParameter(request, "uid", null);
    final String pwd = ServletRequestUtils.getStringParameter(request, "pwd", null);

    if (!application.isConfigured()) {
      String errorMessage = "sventon has not been configured yet!";
      logger.error(errorMessage);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
      return null;
    }

    final RepositoryConfiguration configuration = application.getRepositoryConfiguration(repositoryName);
    if (configuration == null) {
      String errorMessage = "Repository [" + repositoryName + "] does not exist!";
      logger.error(errorMessage);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
      return null;
    }

    SVNRepository repository = null;
    try {
      if (configuration.isAccessControlEnabled()) {
        repository = repositoryFactory.getRepository(configuration.getName(),
            configuration.getSVNURL(), uid, pwd);
      } else {
        repository = repositoryFactory.getRepository(configuration.getName(),
            configuration.getSVNURL(), configuration.getUid(), configuration.getPwd());
      }

      logger.debug("Outputting feed for [" + path + "]");
      final List<SVNLogEntry> logEntries = repositoryService.getRevisions(
          repositoryName, repository, SVNRevision.parse(revision).getNumber(), 1, path, configuration.getRssItemsCount());
      feedGenerator.outputFeed(repositoryName, logEntries, request, response);
    } catch (SVNAuthenticationException ae) {
      logger.info(ERROR_MESSAGE + " - " + ae.getMessage());
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ae.getMessage());
    } catch (SVNException ex) {
      logger.warn(ERROR_MESSAGE, ex);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
    } finally {
      if (repository != null) {
        repository.closeSession();
      }
    }
    return null;
  }

  /**
   * Sets the repository factory instance.
   *
   * @param repositoryFactory Factory.
   */
  public void setRepositoryFactory(final RepositoryFactory repositoryFactory) {
    this.repositoryFactory = repositoryFactory;
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

  /**
   * Sets the repository service instance.
   *
   * @param repositoryService The service instance.
   */
  public void setRepositoryService(final RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

}
