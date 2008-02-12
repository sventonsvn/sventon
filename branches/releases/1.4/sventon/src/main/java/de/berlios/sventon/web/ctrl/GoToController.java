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

import de.berlios.sventon.util.EncodingUtils;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.UserRepositoryContext;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * GoToController.
 * <p/>
 * Controller to inspect the given path/revision combo and forward to appropriate
 * controller.
 * <p/>
 * This controller performs pretty much the same thing as the post handler in
 * {@link AbstractSVNTemplateController}, but can be
 * called as a GET request. This gives a somewhat ugly redundancy that probably
 * should be rmoved.
 *
 * @author patrikfr@users.berlios.de
 * @author jesper@users.berlios.de
 */
public final class GoToController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final StringBuilder redirectUrl = new StringBuilder();
    final SVNNodeKind kind = getRepositoryService().getNodeKind(repository, svnCommand.getPath(), revision.getNumber());
    logger.debug("Node kind of [" + svnCommand.getPath() + "]: " + kind);

    if (kind == SVNNodeKind.DIR) {
      redirectUrl.append("repobrowser.svn");
    } else if (kind == SVNNodeKind.FILE) {
      redirectUrl.append("showfile.svn");
    } else {
      //Invalid path/rev combo. Forward to error page.
      exception.rejectValue("path", "goto.command.invalidpath", "Invalid path");
      return prepareExceptionModelAndView(exception, svnCommand);
    }

    // Add the redirect URL parameters
    redirectUrl.append("?path=").append(EncodingUtils.encodeUrl(svnCommand.getPath()));
    redirectUrl.append("&revision=").append(svnCommand.getRevision());
    redirectUrl.append("&name=").append(svnCommand.getName());
    logger.debug("Redirecting to: " + redirectUrl.toString());
    return new ModelAndView(new RedirectView(redirectUrl.toString()));
  }

}
