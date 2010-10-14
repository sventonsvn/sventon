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
package org.sventon.web.ctrl.template;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.sventon.NoSuchRevisionException;
import org.sventon.SVNConnection;
import org.sventon.model.DirEntry;
import org.sventon.model.UserRepositoryContext;
import org.sventon.util.EncodingUtils;
import org.sventon.web.command.BaseCommand;

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
 * {@link AbstractTemplateController}, but can be
 * called as a GET request. This gives a somewhat ugly redundancy that probably
 * should be removed.
 *
 * @author patrik@sventon.org
 * @author jesper@sventon.org
 */
public final class GoToController extends AbstractTemplateController {

  @Override
  protected ModelAndView svnHandle(final SVNConnection connection, final BaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    String redirectUrl;
    DirEntry.Kind kind = null;

    try {
      kind = getRepositoryService().getNodeKind(connection, command.getPath(), command.getRevisionNumber());
      logger.debug("Node kind of [" + command.getPath() + "]: " + kind);
    } catch (NoSuchRevisionException nsre) {
      logger.info(nsre.getMessage());
    }

    if (DirEntry.Kind.DIR == kind) {
      redirectUrl = command.createListUrl();
    } else if (DirEntry.Kind.FILE == kind) {
      redirectUrl = command.createShowFileUrl();
    } else if (kind == null) {
      exception.rejectValue("revision", "goto.command.invalidrevision");
      return prepareExceptionModelAndView(exception, command);
    } else {
      exception.rejectValue("path", "goto.command.invalidpath");
      return prepareExceptionModelAndView(exception, command);
    }

    // Add the redirect URL parameters
    final Map<String, String> model = new HashMap<String, String>();
    model.put("revision", command.getRevision().toString());

    redirectUrl = EncodingUtils.encodeUrl(redirectUrl);
    logger.debug("Redirecting to: " + redirectUrl);
    return new ModelAndView(new RedirectView(redirectUrl, true), model);
  }

}
