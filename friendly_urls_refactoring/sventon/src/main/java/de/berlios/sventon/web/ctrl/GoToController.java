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
package de.berlios.sventon.web.ctrl;

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
import java.util.HashMap;
import java.util.Map;

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
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    String redirectUrl;
    final SVNNodeKind kind = getRepositoryService().getNodeKind(repository, svnCommand.getPath(), svnCommand.getRevisionNumber());
    logger.debug("Node kind of [" + svnCommand.getPath() + "]: " + kind);

    if (kind == SVNNodeKind.DIR) {
      redirectUrl = "/repos/" + svnCommand.getName().toString() + "/browse" + svnCommand.getPath();
    } else if (kind == SVNNodeKind.FILE) {
      redirectUrl = "/repos/" + svnCommand.getName().toString() + "/view" + svnCommand.getPath();
    } else {
      //Invalid path/rev combo. Forward to error page.
      exception.rejectValue("path", "goto.command.invalidpath");
      return prepareExceptionModelAndView(exception, svnCommand);
    }

    // Add the redirect URL parameters
    final Map<String, String> model = new HashMap<String, String>();
    model.put("revision", SVNRevision.HEAD.equals(svnCommand.getRevision()) ? "HEAD" : String.valueOf(svnCommand.getRevisionNumber()));

    logger.debug("Redirecting to: " + redirectUrl);
    return new ModelAndView(new RedirectView(redirectUrl, true), model);
  }

}
