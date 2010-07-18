/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
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
import java.io.IOException;
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

  @Override
  protected ModelAndView handle(final HttpServletRequest request, final HttpServletResponse response,
                                final Object cmd, final BindException errors) throws Exception {

    logger.debug("Getting RSS feed");

    final BaseCommand command = (BaseCommand) cmd;
    logger.debug(command);

    if (!application.isConfigured()) {
      handleError(response, "sventon has not been configured yet!");
      return null;
    }

    final RepositoryConfiguration configuration = application.getRepositoryConfiguration(command.getName());
    if (configuration == null) {
      handleError(response, "Repository [" + command.getName() + "] does not exist!");
      return null;
    }

    addResponseHeaders(response);

    SVNRepository repository = null;

    try {
      final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
      repository = createRepositoryConnection(request, configuration);
      command.translateRevision(getRepositoryService().getLatestRevision(repository), repository);

      logger.debug("Outputting feed for [" + command.getPath() + "]");
      logEntries.addAll(getRepositoryService().getRevisions(command.getName(), repository, command.getRevisionNumber(),
          FIRST_REVISION, command.getPath(), configuration.getRssItemsCount(), false));
      rssFeedGenerator.outputFeed(configuration, logEntries, request, response);
    } catch (SVNAuthenticationException aex) {
      logger.info(aex.getMessage());
      httpAuthenticationHandler.sendChallenge(response);
    } catch (SVNException svnex) {
      handleSVNException(response, svnex);
    } finally {
      close(repository);
    }
    return null;
  }

  private SVNRepository createRepositoryConnection(final HttpServletRequest request,
                                                   final RepositoryConfiguration configuration) throws SVNException {
    final Credentials credentials = extractCredentials(request, configuration);
    return repositoryConnectionFactory.createConnection(configuration.getName(),
        configuration.getSVNURL(), credentials);
  }


  private void close(SVNRepository repository) {
    if (repository != null) {
      repository.closeSession();
    }
  }

  private void handleSVNException(HttpServletResponse response, SVNException svnex) throws IOException {
    if (SVNErrorCode.FS_NO_SUCH_REVISION == svnex.getErrorMessage().getErrorCode()) {
      logger.info(svnex.getMessage());
    } else {
      logger.error(svnex.getMessage());
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to generate RSS feed");
    }
  }

  private void addResponseHeaders(HttpServletResponse response) {
    response.setContentType(mimeType);
    response.setHeader("Cache-Control", "no-cache");
  }

  private void handleError(HttpServletResponse response, String errorMessage) throws IOException {
    logger.error(errorMessage);
    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
  }

  private Credentials extractCredentials(HttpServletRequest request, RepositoryConfiguration configuration) {
    final Credentials credentialsFromUrlParameters = extractCredentialsFromRequest(request);

    final Credentials credentials;
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
    return credentials;
  }

  private Credentials extractCredentialsFromRequest(HttpServletRequest request) {
    return new Credentials(
        ServletRequestUtils.getStringParameter(request, "userName", null),
        ServletRequestUtils.getStringParameter(request, "userPassword", null));
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
