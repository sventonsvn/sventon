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

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.model.LogEntryWrapper;
import org.sventon.model.UserRepositoryContext;
import org.sventon.web.command.BaseCommand;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
   * Max entries per page, default set to 20.
   */
  private int pageSize = 20;

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
  protected ModelAndView svnHandle(final SVNRepository repository, final BaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final String nextPath = ServletRequestUtils.getStringParameter(request, "nextPath", command.getPath());
    final SVNRevision nextRevision = SVNRevision.parse(ServletRequestUtils.getStringParameter(
        request, "nextRevision", command.getRevision().toString()));
    final boolean stopOnCopy = ServletRequestUtils.getBooleanParameter(request, "stopOnCopy", true);
    final long fromRevision = calculateFromRevision(headRevision, nextRevision);

    final List<LogEntryWrapper> logEntryWrappers = new ArrayList<LogEntryWrapper>();

    try {
      final List<SVNLogEntry> logEntries = getRepositoryService().getRevisions(command.getName(), repository,
          fromRevision, FIRST_REVISION, nextPath, pageSize, stopOnCopy);

      String pathAtRevision = nextPath;

      for (final SVNLogEntry logEntry : logEntries) {
        logEntryWrappers.add(new LogEntryWrapper(logEntry, pathAtRevision));

        //noinspection unchecked
        final Map<String, SVNLogEntryPath> allChangedPaths = logEntry.getChangedPaths();
        final Set<String> changedPaths = allChangedPaths.keySet();

        for (String entryPath : changedPaths) {
          int i = StringUtils.indexOfDifference(entryPath, pathAtRevision);
          if (i == -1) { // Same path
            final SVNLogEntryPath logEntryPath = allChangedPaths.get(entryPath);
            if (logEntryPath.getCopyPath() != null) {
              pathAtRevision = logEntryPath.getCopyPath();
            }
          } else if (entryPath.length() == i) { // Part path, can be a branch
            final SVNLogEntryPath logEntryPath = allChangedPaths.get(entryPath);
            if (logEntryPath.getCopyPath() != null) {
              pathAtRevision = logEntryPath.getCopyPath() + pathAtRevision.substring(i);
            }
          }
        }
      }
    } catch (SVNException svnex) {
      if (SVNErrorCode.FS_NO_SUCH_REVISION == svnex.getErrorMessage().getErrorCode()) {
        logger.info(svnex.getMessage());
      } else {
        logger.error(svnex.getMessage());
      }
    }

    final Map<String, Object> model = new HashMap<String, Object>();

    model.put("stopOnCopy", stopOnCopy);
    model.put("logEntriesPage", logEntryWrappers);
    model.put("pageSize", pageSize);
    model.put("isFile", getRepositoryService().getNodeKind(repository, command.getPath(), command.getRevisionNumber()) == SVNNodeKind.FILE);
    model.put("morePages", logEntryWrappers.size() == pageSize);
    model.put("nextPath", nextPath);
    model.put("nextRevision", fromRevision);
    return new ModelAndView(getViewName(), model);
  }

  protected long calculateFromRevision(long headRevision, SVNRevision nextRevision) {
    final long fromRevision;
    if (SVNRevision.HEAD.equals(nextRevision)) {
      fromRevision = headRevision;
    } else {
      fromRevision = nextRevision.getNumber();
    }
    return fromRevision;
  }
}
