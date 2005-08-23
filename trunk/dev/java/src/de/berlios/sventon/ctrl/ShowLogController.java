package de.berlios.sventon.ctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
 * ShowLogController. For showing logs. Note, this currently does not work for
 * protocol http/https.
 * <p>
 * The log entries will be paged if the number of entries exceeds max page siz,
 * {@link #PAGE_SIZE}. Paged log entries are stored in the user HTTP session
 * using key <code>sventon.logEntryPages</code>. The type of this object is
 * <code>List<List<SVNLogEntry>></code>.
 * 
 * @author patrikfr@users.berlios.de
 */
public class ShowLogController extends AbstractSVNTemplateController implements Controller {

  // Page size, this is the max number of log entires shown at a time
  public static final int PAGE_SIZE = 10;

  public static final String PAGES_SESSION_KEY = "sventon.logEntryPages";
  public static final String PAGES_COMMAND_SESSION_KEY = "sventon.logEntryPagesCommand";

  // private static final SVNRevision FIRST_REVISION = SVNRevision.create(1);

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
      HttpServletRequest request, HttpServletResponse response) throws SVNException {

    // Remove any stale pagees from the session, if any
    HttpSession session = request.getSession(true);
    session.removeAttribute(PAGES_SESSION_KEY);
    session.removeAttribute(PAGES_COMMAND_SESSION_KEY);

    String path = svnCommand.getPath();

    if (!path.startsWith("/")) {
      path = "/" + path;
    }

    String[] targetPaths = new String[] { path };
    String pathAtRevision = targetPaths[0];

    List<LogEntryBundle> logEntryBundles = new ArrayList<LogEntryBundle>();
    

    logger.debug("Assembling logs data");
    // TODO: Safer parsing would be nice.
    List<SVNLogEntry> logEntries = (List<SVNLogEntry>) repository.log(targetPaths, null, revision.getNumber(), 0, true,
        false);
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
        } else if (entryPath.length() == i) { // Part path, can be a
          // branch
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

    List<List<LogEntryBundle>> pages = null;

    pages = pageLogEntries(logEntryBundles);

    if (pages.size() > 1) {
      // TODO: SVNLogEntry objects are not Serializable and should not really be
      // stored in the session, but it will have to do for now. In the future:
      // create own wrapper objects for SVNLogEntry
      session.setAttribute(PAGES_SESSION_KEY, pages);
      session.setAttribute(PAGES_COMMAND_SESSION_KEY, svnCommand);
    }

    model.put("logEntriesPage", pages.get(0));
    model.put("isFile", nodeKind == SVNNodeKind.FILE);
    model.put("pageCount", pages.size());
    model.put("pageNumber", 1);

    return new ModelAndView("showlog", model);
  }

  /**
   * Create pages of the given log entries, the pages are stored as lists of
   * <code>SVNLogEntry</code>.
   * <p>
   * Visibility set to protected for testing purposes...
   * 
   * @param logEntries List of <code>SVNLogEntry</code> to page.
   * @return List of pages, there will always be at least one (possibly empty)
   *         page.
   */
  protected List<List<LogEntryBundle>> pageLogEntries(final List<LogEntryBundle> logEntries) {

    if (logEntries == null) {
      throw new NullPointerException("Parameter 'logEntries' must not be null");
    }

    List<List<LogEntryBundle>> pages = new ArrayList<List<LogEntryBundle>>();
    for (int i = 0; i < logEntries.size(); i = i + PAGE_SIZE) {
      int start = i;
      int end = ((i + PAGE_SIZE) < logEntries.size()) ? i + PAGE_SIZE : logEntries.size();
      pages.add(new ArrayList<LogEntryBundle>(logEntries.subList(start, end)));
    }

    if (pages.size() == 0) {
      pages.add(new ArrayList<LogEntryBundle>());
    }

    return pages;
  }
}
