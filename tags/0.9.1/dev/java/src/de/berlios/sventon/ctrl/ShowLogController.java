/*
 * ====================================================================
 * Copyright (c) 2005 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.ctrl;

import static org.tmatesoft.svn.core.wc.SVNRevision.HEAD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import de.berlios.sventon.command.SVNBaseCommand;

/**
 * ShowLogController. For showing logs. Note, this currently does not work for
 * protocol http/https. <p/> The log entries will be paged if the number of
 * entries exceeds max page siz, {@link #pageSize}. Paged log entries are
 * stored in the user HTTP session using key <code>sventon.logEntryPages</code>.
 * The type of this object is <code>List<List<SVNLogEntry>></code>.
 * 
 * @author patrikfr@users.berlios.de
 */
public class ShowLogController extends AbstractSVNTemplateController implements Controller {

  //Max entries / page
  private int pageSize = 50;
  
  /**
   * Set page size, this is the max number of log entires shown at a time
   * @param pageSize Page size.
   */
  public void setPageSize(final int pageSize) {
    this.pageSize = pageSize;
  }
 

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
      HttpServletRequest request, HttpServletResponse response, BindException exception) throws SVNException {

    String path = svnCommand.getCompletePath();

    String nextPathParam = request.getParameter("nextPath");
    String nextRevParam = request.getParameter("nextRevision");

    String[] targetPaths;
    long revNumber = 0;

    if (!path.startsWith("/")) {
      path = "/" + path;
    }

    if (nextPathParam == null || nextRevParam == null) {
      targetPaths = new String[] { path };
      long revNumber1;
      if (revision == HEAD)
        revNumber1 = repository.getLatestRevision();
      else
        revNumber1 = revision.getNumber();
      revNumber = revNumber1;
    } else {

      targetPaths = new String[] { nextPathParam };

      if ("HEAD".equals(nextRevParam)) {
        revNumber = repository.getLatestRevision();
      } else {
        try {
          revNumber = Long.parseLong(nextRevParam);
        } catch (NumberFormatException nfe) {
          exception.reject("log.command.invalidpath", "Invalid revision/path combination for logs");
          return prepareExceptionModelAndView(exception, svnCommand);
        }
      }
    }

    String pathAtRevision = targetPaths[0];

    List<LogEntryBundle> logEntryBundles = new ArrayList<LogEntryBundle>();

    logger.debug("Assembling logs data");
    // TODO: Safer parsing would be nice.

    final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();

    repository.log(targetPaths, revNumber, 0, true, false, pageSize, new ISVNLogEntryHandler() {

      public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
        logEntries.add(logEntry);
      }

    });

    SVNNodeKind nodeKind = repository.checkPath(path, revision.getNumber());

    for (SVNLogEntry logEntry : logEntries) {

      logEntryBundles.add(new LogEntryBundle(logEntry, pathAtRevision));
      Map<String, SVNLogEntryPath> m = logEntry.getChangedPaths();
      Set<String> changedPaths = m.keySet();
      for (String entryPath : changedPaths) {
        int i = StringUtils.indexOfDifference(entryPath, pathAtRevision);
        if (i == -1) { // Same path
          SVNLogEntryPath logEntryPath = m.get(entryPath);
          if (logEntryPath.getCopyPath() != null) {
            pathAtRevision = logEntryPath.getCopyPath();
          }
        } else if (entryPath.length() == i) { // Part path, can be a branch
          SVNLogEntryPath logEntryPath = m.get(entryPath);
          if (logEntryPath.getCopyPath() != null) {
            pathAtRevision = logEntryPath.getCopyPath() + pathAtRevision.substring(i);
          }
        }
      }
    }

    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();

    model.put("logEntriesPage", logEntryBundles);
    model.put("pageSize", pageSize);
    model.put("isFile", nodeKind == SVNNodeKind.FILE);
    model.put("morePages", logEntryBundles.size() == pageSize);
    model.put("properties", new HashMap()); // TODO: Replace with valid entry
    // properties
    return new ModelAndView("showlog", model);
  }
}
