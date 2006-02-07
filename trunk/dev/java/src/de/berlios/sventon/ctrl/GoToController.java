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
package de.berlios.sventon.ctrl;

import de.berlios.sventon.command.SVNBaseCommand;
import de.berlios.sventon.svnsupport.SventonException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;
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
 * {@link AbstractSVNTemplateController}, but can be
 * called as a GET request. This gives a somewhat ugly redundancy that probably
 * should be rmoved.
 *
 * @author patrikfr@users.berlios.de
 */
public class GoToController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository,
                                   SVNBaseCommand svnCommand, SVNRevision revision, HttpServletRequest request,
                                   HttpServletResponse response, BindException exception) throws SventonException, SVNException {

    String redirectUrl = null;

    logger.debug("Checking node kind for command: " + svnCommand);
    SVNNodeKind kind = repository.checkPath(svnCommand.getPath(), revision.getNumber());

    logger.debug("Node kind: " + kind);

    if (kind == SVNNodeKind.DIR) {
      redirectUrl = "repobrowser.svn";
    } else if (kind == SVNNodeKind.FILE) {
      redirectUrl = "showfile.svn";
    } else {
      //Invalid path/rev combo. Forward to error page.
      exception.rejectValue("path", "goto.command.invalidpath", "Invalid path");
      return prepareExceptionModelAndView(exception, svnCommand);
    }
    logger.debug("Submitted command: " + svnCommand);
    logger.debug("Redirecting to: " + redirectUrl);

    Map<String, Object> m = new HashMap<String, Object>();
    m.put("path", svnCommand.getPath());
    m.put("revision", svnCommand.getRevision());
    return new ModelAndView(new RedirectView(redirectUrl), m);
  }

}
