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
package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.UserContext;
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
                                   final SVNRevision revision, final UserContext userContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final String redirectUrl;
    final SVNNodeKind kind = getRepositoryService().getNodeKind(repository, svnCommand.getPath(), revision.getNumber());
    logger.debug("Node kind of [" + svnCommand.getPath() + "]: " + kind);

    if (kind == SVNNodeKind.DIR) {
      redirectUrl = "repobrowser.svn";
    } else if (kind == SVNNodeKind.FILE) {
      redirectUrl = "showfile.svn";
    } else {
      //Invalid path/rev combo. Forward to error page.
      exception.rejectValue("path", "goto.command.invalidpath", "Invalid path");
      return prepareExceptionModelAndView(exception, svnCommand);
    }

    // Populate model and view with basic data
    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("path", svnCommand.getPath());
    model.put("revision", svnCommand.getRevision());
    model.put("name", svnCommand.getName());
    logger.debug("Redirecting to: " + redirectUrl);
    return new ModelAndView(new RedirectView(redirectUrl), model);
  }

}
