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
package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.model.LogEntryBundle;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.UserRepositoryContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;
import static org.tmatesoft.svn.core.wc.SVNRevision.HEAD;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Controller for showing logs.
 * <p/>
 * The log entries will be paged if the number of entries exceeds max page size, {@link #pageSize}.
 *
 * @author patrikfr@users.berlios.de
 * @author jesper@users.berlios.de
 */
public final class ShowLogController extends AbstractSVNTemplateController implements Controller {

  /**
   * Max entries per page, default set to 50.
   */
  private int pageSize = 50;

  /**
   * Set page size.
   * Max number of log entires shown at a time.
   *
   * @param pageSize Page size.
   */
  public void setPageSize(final int pageSize) {
    this.pageSize = pageSize;
  }

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    String path = svnCommand.getPath();

    final String nextPathParam = ServletRequestUtils.getStringParameter(request, "nextPath", null);
    final String nextRevParam = ServletRequestUtils.getStringParameter(request, "nextRevision", null);

    final String targetPath;
    final long revNumber;

    if (!path.startsWith("/")) {
      path = "/" + path;
    }

    if (nextPathParam == null || nextRevParam == null) {
      targetPath = path;
      revNumber = revision == HEAD ? getRepositoryService().getLatestRevision(repository) : revision.getNumber();
    } else {
      targetPath = nextPathParam;
      if ("HEAD".equals(nextRevParam)) {
        revNumber = getRepositoryService().getLatestRevision(repository);
      } else {
        try {
          revNumber = Long.parseLong(nextRevParam);
        } catch (final NumberFormatException nfe) {
          exception.reject("log.command.invalidpath", "Invalid revision/path combination for logs");
          return prepareExceptionModelAndView(exception, svnCommand);
        }
      }
    }

    final List<LogEntryBundle> logEntryBundles = new ArrayList<LogEntryBundle>();
    final long toRevision = 1;

    logger.debug("Assembling logs data revision [" + revNumber + " - " + toRevision + "] limit: " + pageSize);

    // TODO: Safer parsing would be nice.
    final List<SVNLogEntry> logEntries =
        getRepositoryService().getRevisions(svnCommand.getName(), repository, revNumber, toRevision, targetPath, pageSize);

    String pathAtRevision = targetPath;

    for (final SVNLogEntry logEntry : logEntries) {
      logEntryBundles.add(new LogEntryBundle(logEntry, pathAtRevision));
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

    final Map<String, Object> model = new HashMap<String, Object>();

    model.put("logEntriesPage", logEntryBundles);
    model.put("pageSize", pageSize);
    model.put("isFile", getRepositoryService().getNodeKind(repository, path, revision.getNumber()) == SVNNodeKind.FILE);
    model.put("morePages", logEntryBundles.size() == pageSize);
    return new ModelAndView("showLog", model);
  }
}
