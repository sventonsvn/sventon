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
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.SVNConnection;
import org.sventon.model.LogEntry;
import org.sventon.model.Revision;
import org.sventon.model.UserRepositoryContext;
import org.sventon.web.command.BaseCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gets the file revision history for a given entry.
 *
 * @author jesper@sventon.org
 */
public final class GetFileHistoryController extends AbstractTemplateController {

  /**
   * Request parameter identifying the archived entry to display.
   */
  static final String ARCHIVED_ENTRY = "archivedEntry";
  private static final int FILE_HISTORY_LIMIT = 100;

  @Override
  protected ModelAndView svnHandle(final SVNConnection connection, final BaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final Map<String, Object> model = new HashMap<String, Object>();
    final String archivedEntry = ServletRequestUtils.getStringParameter(request, ARCHIVED_ENTRY, null);

    logger.debug("Finding revisions for [" + command.getPath() + "]");
    final List<LogEntry> revisions = getRepositoryService().getLogEntries(command.getName(), connection,
        command.getRevisionNumber(), Revision.FIRST.getNumber(), command.getPath(), FILE_HISTORY_LIMIT, false, true);
    LogEntry.setPathAtRevisionInLogEntries(revisions, command.getPath());

    model.put("currentRevision", command.getRevisionNumber());
    model.put("logEntries", revisions);
    if (archivedEntry != null) {
      model.put(ARCHIVED_ENTRY, archivedEntry);
    }

    return new ModelAndView(getViewName(), model);
  }
}
