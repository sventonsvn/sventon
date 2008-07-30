/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.ctrl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.sventon.RepositoryFactory;
import org.sventon.appl.Application;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.model.RepositoryName;
import org.sventon.rss.RssFeedGenerator;
import org.sventon.service.RepositoryService;
import static org.sventon.web.ctrl.template.AbstractSVNTemplateController.FIRST_REVISION;
import org.tmatesoft.svn.core.SVNAuthenticationException;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller used for generating RSS feeds.
 *
 * @author jesper@sventon.org
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
  private RssFeedGenerator rssFeedGenerator;

  /**
   * Service.
   */
  private RepositoryService repositoryService;

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
    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    try {
      if (configuration.isAccessControlEnabled()) {
        repository = repositoryFactory.getRepository(configuration.getName(),
            configuration.getSVNURL(), uid, pwd);
      } else {
        repository = repositoryFactory.getRepository(configuration.getName(),
            configuration.getSVNURL(), configuration.getUid(), configuration.getPwd());
      }

      logger.debug("Outputting feed for [" + path + "]");
      final long revisionNumber = SVNRevision.parse(revision).getNumber();
      logEntries.addAll(repositoryService.getRevisions(repositoryName, repository, revisionNumber, FIRST_REVISION, path,
          configuration.getRssItemsCount()));
    } catch (SVNAuthenticationException aex) {
      logger.info(aex.getMessage());
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, aex.getMessage());
    } catch (SVNException svnex) {
      if (SVNErrorCode.FS_NO_SUCH_REVISION == svnex.getErrorMessage().getErrorCode()) {
        logger.info(svnex.getMessage());
      } else {
        logger.error(svnex.getMessage());
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to generate RSS feed");
      }
    } finally {
      if (repository != null) {
        repository.closeSession();
      }
    }

    rssFeedGenerator.outputFeed(configuration, logEntries, request, response);
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
   * @param rssFeedGenerator The generator.
   */
  public void setRssFeedGenerator(final RssFeedGenerator rssFeedGenerator) {
    this.rssFeedGenerator = rssFeedGenerator;
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
