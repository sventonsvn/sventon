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
package org.sventon.web.ctrl.template;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.sventon.model.UserRepositoryContext;
import org.sventon.util.EncodingUtils;
import org.sventon.web.command.SVNBaseCommand;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * GoToController.
 * <p/>
 * Controller to inspect the given path/revision combo and forward to appropriate
 * controller.
 * <p/>
 * This controller performs pretty much the same thing as the post handler in
 * {@link org.sventon.web.ctrl.template.AbstractSVNTemplateController}, but can be
 * called as a GET request. This gives a somewhat ugly redundancy that probably
 * should be rmoved.
 *
 * @author patrik@sventon.org
 * @author jesper@sventon.org
 */
public final class GoToController extends AbstractSVNTemplateController {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    String redirectUrl;
    SVNNodeKind kind = null;

    try {
      kind = getRepositoryService().getNodeKind(repository, command.getPath(), command.getRevisionNumber());
      logger.debug("Node kind of [" + command.getPath() + "]: " + kind);
    } catch (SVNException svnex) {
      if (SVNErrorCode.FS_NO_SUCH_REVISION == svnex.getErrorMessage().getErrorCode()) {
        logger.info(svnex.getMessage());
      } else {
        logger.error(svnex.getMessage());
      }
    }

    if (SVNNodeKind.DIR == kind) {
      redirectUrl = createBrowseUrl(command);
    } else if (SVNNodeKind.FILE == kind) {
      redirectUrl = createViewUrl(command);
    } else if (kind == null) {
      exception.rejectValue("revision", "goto.command.invalidrevision");
      return prepareExceptionModelAndView(exception, command);
    } else {
      exception.rejectValue("path", "goto.command.invalidpath");
      return prepareExceptionModelAndView(exception, command);
    }

    // Add the redirect URL parameters
    final Map<String, String> model = new HashMap<String, String>();
    model.put("revision", SVNRevision.HEAD.equals(command.getRevision()) ? "HEAD" : String.valueOf(command.getRevisionNumber()));

    redirectUrl = EncodingUtils.encodeUrl(redirectUrl);
    logger.debug("Redirecting to: " + redirectUrl);
    return new ModelAndView(new RedirectView(redirectUrl, true), model);
  }

  /**
   * Creates a redirect url for browsing a directory.
   * <p/>
   * Note: A trailing slash ("/") will be appended if missing on path.
   *
   * @param command Command
   * @return Url
   */
  protected String createBrowseUrl(final SVNBaseCommand command) {
    String path = command.getPath();
    if (!path.endsWith("/")) {
      path += "/";
    }
    return "/repos/" + command.getName().toString() + "/browse" + path;
  }

  /**
   * Creates a redirect url for viewing a file.
   * <p/>
   * Note: A trailing slash ("/") will be removed if found on path.
   *
   * @param command Command
   * @return Url
   */
  protected String createViewUrl(final SVNBaseCommand command) {
    String path = command.getPath();
    if (path.endsWith("/")) {
      path = path.substring(0, path.length() - 1);
    }
    return "/repos/" + command.getName().toString() + "/view" + path;
  }
}
