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
import org.sventon.SVNConnection;
import org.sventon.model.LogEntry;
import org.sventon.model.LogMessageSearchItem;
import org.sventon.model.UserRepositoryContext;
import org.sventon.web.command.BaseCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Gets the log message for given revision.
 *
 * @author jesper@sventon.org
 */
public final class GetLogMessageController extends AbstractTemplateController {

  @Override
  protected ModelAndView svnHandle(final SVNConnection connection, final BaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final Map<String, Object> model = new HashMap<String, Object>();
    final long revisionNumber = command.getRevisionNumber();
    logger.debug("Getting log message from revision [" + revisionNumber + "]");
    final LogEntry logEntry = getRepositoryService().getLogEntry(command.getName(), connection, revisionNumber);
    final LogMessageSearchItem searchItem = new LogMessageSearchItem(logEntry); //TODO: Parse to apply Bugtraq link
    model.put("logEntry", searchItem);
    return new ModelAndView(getViewName(), model);
  }
}
