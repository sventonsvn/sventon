/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.ctrl;

import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.model.Credentials;
import org.sventon.rss.RssFeedGenerator;
import org.sventon.web.HttpAuthenticationHandler;
import org.sventon.web.command.BaseCommand;
import static org.sventon.web.ctrl.template.AbstractTemplateController.FIRST_REVISION;
import org.tmatesoft.svn.core.SVNAuthenticationException;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller used for generating RSS feeds.
 *
 * @author jesper@sventon.org
 */
public final class RSSController extends AbstractBaseController {

  /**
   * RSS mime type, default set to <tt>application/xml; charset=UTF-8</tt>.
   */
  private String mimeType = "application/xml; charset=UTF-8";

  /**
   * The feed generator.
   */
  private RssFeedGenerator rssFeedGenerator;

  /**
   * HTTP Authentication Handler.
   */
  private HttpAuthenticationHandler httpAuthenticationHandler;

  /**
   * {@inheritDoc}
   */
  protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response,
                                final Object cmd, final BindException errors) throws Exception {

    logger.debug("Getting RSS feed");

    final BaseCommand command = (BaseCommand) cmd;
    logger.debug(command);

    final Credentials credentialsFromUrlParameters = new Credentials(
        ServletRequestUtils.getStringParameter(request, "userName", null),
        ServletRequestUtils.getStringParameter(request, "userPassword", null));

    if (!application.isConfigured()) {
      String errorMessage = "sventon has not been configured yet!";
      logger.error(errorMessage);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
      return null;
    }

    final RepositoryConfiguration configuration = application.getRepositoryConfiguration(command.getName());
    if (configuration == null) {
      String errorMessage = "Repository [" + command.getName() + "] does not exist!";
      logger.error(errorMessage);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
      return null;
    }

    response.setContentType(mimeType);
    response.setHeader("Cache-Control", "no-cache");

    SVNRepository repository = null;
    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    final Credentials credentials;

    try {
      if (configuration.isAccessControlEnabled()) {
        if (httpAuthenticationHandler.isLoginAttempt(request)) {
          logger.debug("Basic HTTP authentication detected. Parsing credentials from request.");
          credentials = httpAuthenticationHandler.parseCredentials(request);
        } else {
          logger.debug("Parsing credentials from url");
          credentials = credentialsFromUrlParameters;
        }
      } else {
        credentials = configuration.getUserCredentials();
      }
      repository = repositoryConnectionFactory.createConnection(configuration.getName(),
          configuration.getSVNURL(), credentials);

      command.translateRevision(getRepositoryService().getLatestRevision(repository), repository);

      logger.debug("Outputting feed for [" + command.getPath() + "]");
      logEntries.addAll(getRepositoryService().getRevisions(command.getName(), repository, command.getRevisionNumber(),
          FIRST_REVISION, command.getPath(), configuration.getRssItemsCount(), false));
      rssFeedGenerator.outputFeed(configuration, logEntries, request, response);
    } catch (SVNAuthenticationException aex) {
      logger.info(aex.getMessage());
      httpAuthenticationHandler.sendChallenge(response);
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
    return null;
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
   * Sets the authentication handler to use.
   *
   * @param httpAuthenticationHandler Handler
   */
  public void setHttpAuthenticationHandler(final HttpAuthenticationHandler httpAuthenticationHandler) {
    this.httpAuthenticationHandler = httpAuthenticationHandler;
  }
}
