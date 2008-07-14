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

import org.sventon.web.command.SVNBaseCommand;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.model.LogMessage;
import org.sventon.model.UserRepositoryContext;
import org.sventon.web.ctrl.template.AbstractSVNTemplateController;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Gets the log message for given revision.
 *
 * @author jesper@users.berlios.de
 */
public final class GetLogMessageController extends AbstractSVNTemplateController {

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    logger.debug("Getting log message from revision [" + svnCommand.getRevisionNumber() + "]");
    final SVNLogEntry logEntry = getRepositoryService().getRevision(svnCommand.getName(), repository, svnCommand.getRevisionNumber());
    final LogMessage logMessage = new LogMessage(svnCommand.getRevisionNumber(), logEntry.getMessage()); //TODO: Parse to apply Bugtraq link

    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("logMessage", logMessage);
    return new ModelAndView("ajax/logMessage", model);
  }
}
