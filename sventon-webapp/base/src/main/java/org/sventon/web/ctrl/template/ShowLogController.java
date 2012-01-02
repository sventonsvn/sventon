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
import org.sventon.NoSuchRevisionException;
import org.sventon.SVNConnection;
import org.sventon.model.DirEntry;
import org.sventon.model.LogEntry;
import org.sventon.web.UserRepositoryContext;
import org.sventon.web.command.BaseCommand;
import org.sventon.web.command.LogCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for showing logs.
 * <p/>
 * The log entries will be paged if the number of entries exceeds max page size, {@link #pageSize}.
 *
 * @author patrik@sventon.org
 * @author jesper@sventon.org
 */
public final class ShowLogController extends AbstractTemplateController {

  /**
   * Max entries per page, default set to 25.
   */
  private int pageSize = 25;

  /**
   * Set page size.
   * Max number of log entries shown at a time.
   *
   * @param pageSize Page size.
   */
  public void setPageSize(final int pageSize) {
    this.pageSize = pageSize;
  }

  @Override
  protected ModelAndView svnHandle(final SVNConnection connection, final BaseCommand cmd,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {


    final LogCommand command = (LogCommand) cmd;
    final long fromRevision = command.calculateFromRevision(headRevision);
    final String nextPath = command.calculateNextPath();
    final int limit = command.isPaging() ? pageSize : -1;

    final List<LogEntry> logEntries = new ArrayList<LogEntry>();
    try {
      logEntries.addAll(getRepositoryService().getLogEntries(connection, command.getName(), fromRevision,
          command.getStopRevision().getNumber(), nextPath, limit, command.isStopOnCopy(), true));
      LogEntry.setPathAtRevisionInLogEntries(logEntries, nextPath);
    } catch (NoSuchRevisionException nsre) {
      logger.info(nsre.getMessage());
    }

    final Map<String, Object> model = new HashMap<String, Object>();
    final DirEntry.Kind nodeKind = getRepositoryService().getNodeKind(connection, command.getPath(), command.getRevisionNumber());
    model.put("logEntriesPage", logEntries);
    model.put("isFile", DirEntry.Kind.FILE == nodeKind);
    model.put("stopOnCopy", command.isStopOnCopy());
    model.put("paging", command.isPaging());
    model.put("stopRevision", command.getStopRevision());

    if (command.isPaging()) {
      model.put("pageSize", pageSize);
      model.put("morePages", logEntries.size() == pageSize);
      model.put("nextPath", nextPath);
      model.put("nextRevision", fromRevision);
    }

    return new ModelAndView(getViewName(), model);
  }

}
