/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
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
import org.sventon.NoSuchRevisionException;
import org.sventon.SVNConnection;
import org.sventon.SventonException;
import org.sventon.model.LogEntry;
import org.sventon.web.UserRepositoryContext;
import org.sventon.web.command.BaseCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gets the <i>N</i> latest revision details.
 *
 * @author jesper@sventon.org
 */
public final class GetLatestRevisionsController extends AbstractTemplateController {

  @Override
  protected ModelAndView svnHandle(final SVNConnection connection, final BaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final Map<String, Object> model = new HashMap<String, Object>();
    final List<LogEntry> revisions = new ArrayList<LogEntry>();
    final int revisionCount = userRepositoryContext.getLatestRevisionsDisplayCount();

    try {
      logger.debug("Getting [" + revisionCount + "] latest revisions");
      final List<LogEntry> logEntries = getRepositoryService().getLatestRevisions(connection, command.getName(), revisionCount);

      //TODO: Parse to apply Bugtraq links
      revisions.addAll(logEntries);
      logger.debug("Got [" + revisions.size() + "] revisions");
    } catch (NoSuchRevisionException nsre) {
      logger.info(nsre.getMessage());
      model.put("errorMessage", "There are no commits in this repository yet.");
    } catch (SventonException svnex) {
      logger.error(svnex.getMessage());
      model.put("errorMessage", svnex.getMessage());
    }
    model.put("revisions", revisions);
    return new ModelAndView(getViewName(), model);
  }
}
