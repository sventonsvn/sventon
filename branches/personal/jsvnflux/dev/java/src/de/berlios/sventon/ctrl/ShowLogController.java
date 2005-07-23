package de.berlios.sventon.ctrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * ShowLogController.
 * @author patrikfr@users.berlios.de
 */
public class ShowLogController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
      HttpServletRequest request, HttpServletResponse response) throws SVNException {

    String path = svnCommand.getPath();
    
    if (!path.startsWith("/")) {
      path = "/" + path;
    }
    
    String[] targetPaths = new String[] { path };
    String pathAtRevision = targetPaths[0];

    List<SVNLogEntry> logEntries = null;
    List<LogEntryBundle> logEntryBundles = new ArrayList<LogEntryBundle>();
    SVNNodeKind nodeKind = null;

    logger.debug("Assembling logs data");
    //TODO: Safer parsing would be nice.
    logEntries = (List<SVNLogEntry>) repository.log(targetPaths, null, revision.getNumber(), 0,
        true, false);
    nodeKind = repository.checkPath(path, revision.getNumber());

    for (SVNLogEntry logEntry : logEntries) {
      
      logEntryBundles.add(new LogEntryBundle(logEntry, pathAtRevision));
      Map<String, SVNLogEntryPath> m = logEntry.getChangedPaths();
      Set<String> changedPaths = m.keySet();
      for (String entryPath : changedPaths) {
        int i = StringUtils.indexOfDifference(entryPath, pathAtRevision);
        if (i == -1) { //Same path
          SVNLogEntryPath logEntryPath = m.get(entryPath);
          if (logEntryPath.getCopyPath() != null) {
            pathAtRevision = logEntryPath.getCopyPath();
          }
        } else if (entryPath.length() == i) { //Part path, can be a branch
          SVNLogEntryPath logEntryPath = m.get(entryPath);
          if (logEntryPath.getCopyPath() != null) {
            pathAtRevision = logEntryPath.getCopyPath() + pathAtRevision.substring(i);
          }
        } else {
          continue;
        }
      }
    }

    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();

    Collections.reverse(logEntries);
    model.put("logEntries", logEntryBundles);
    model.put("isFile", nodeKind == SVNNodeKind.FILE);

    return new ModelAndView("showlog", model);
  }
}
