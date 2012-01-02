/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
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
import org.sventon.web.UserRepositoryContext;
import org.sventon.web.command.BaseCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller to fetch details for a specific revision.
 *
 * @author jesper@sventon.org
 */
public final class ShowRevisionInfoController extends AbstractTemplateController {

  @Override
  protected ModelAndView svnHandle(final SVNConnection connection, final BaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final long revisionNumber = command.getRevisionNumber();
    logger.debug("Getting revision info details for revision: " + revisionNumber);
    final LogEntry logEntry = getRepositoryService().getLogEntry(connection, command.getName(), revisionNumber);
    //TODO: Parse to apply Bugtraq link
    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("revisionInfo", logEntry);

    return new ModelAndView(getViewName(), model);
  }

}
