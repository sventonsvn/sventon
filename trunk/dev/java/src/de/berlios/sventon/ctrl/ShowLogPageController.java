package de.berlios.sventon.ctrl;

import static de.berlios.sventon.ctrl.ShowLogController.PAGES_COMMAND_SESSION_KEY;
import static de.berlios.sventon.ctrl.ShowLogController.PAGES_SESSION_KEY;
import de.berlios.sventon.command.SVNBaseCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Controller to show paged logs views.
 * <p>
 * This controller expects to find paged logs in the HTTP session as a
 * <code>List&lt;List&lt;LogEntryBundle&gt;&gt;</code> object, store using key
 * <code>{@link ShowLogController#PAGES_SESSION_KEY}</code>. Also the
 * original <code>{@link de.berlios.sventon.command.SVNBaseCommand}</code> object
 * must be stored in the session using using key
 * <code>{@link ShowLogController#PAGES_COMMAND_SESSION_KEY}</code>. The latter object is used to check if the user
 * has been tampering with parameter data. 
 * 
 * @author patrikfr@users.berlios.de
 */
public class ShowLogPageController extends AbstractSVNTemplateController {

  @SuppressWarnings("unchecked")
  @Override
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
      HttpServletRequest request, HttpServletResponse response) throws SVNException {

    HttpSession session = request.getSession(true);
    List<List<LogEntryBundle>> pages = (List<List<LogEntryBundle>>) session.getAttribute(PAGES_SESSION_KEY);

    // Retrieve original logs command, to detect tampering of request data.
    SVNBaseCommand logsCommand = (SVNBaseCommand) session.getAttribute(PAGES_COMMAND_SESSION_KEY);

    String path = svnCommand.getCompletePath();
    if (!path.startsWith("/")) {
      path = "/" + path;
    }

    Integer parsedPageNumber = null;
    String pageNumber = request.getParameter("page");

    if (pageNumber != null) {
      try {
        parsedPageNumber = Integer.valueOf(pageNumber);
      } catch (NumberFormatException nfe) {
        // Ignore
      }
    }

    final SVNNodeKind nodeKind = repository.checkPath(path, revision.getNumber());
    if (nodeKind == SVNNodeKind.NONE) {
      throw new SVNException("Illegal revision/path combination");
    }

    logger.debug("Create model");
    final Map<String, Object> model = new HashMap<String, Object>();

    model.put("isFile", nodeKind == SVNNodeKind.FILE);

    if (isInvalidParams(pages, parsedPageNumber, svnCommand, logsCommand)) {
      model.put("logEntriesPage", new ArrayList<LogEntryBundle>());
      model.put("pageCount", 0);
      model.put("pageNumber", 0);
    } else {
      model.put("logEntriesPage", pages.get(parsedPageNumber - 1));
      model.put("pageCount", pages.size());
      model.put("pageNumber", parsedPageNumber);
    }
    return new ModelAndView("showlog", model);

  }

  /**
   * Check if given parameters are valid or not.
   * 
   * @param pages Paged log entries
   * @param parsedPageNumber Requested page number.
   * @param currentCmd Command for this request.
   * @param storedCmd Command stored from original logs request.
   * @return <code>true</code> if the combination of parameters are invalid.
   */
  private boolean isInvalidParams(List<List<LogEntryBundle>> pages, Integer parsedPageNumber,
      SVNBaseCommand currentCmd, SVNBaseCommand storedCmd) {
    boolean invalidParams = pages == null || parsedPageNumber == null || pages.size() < parsedPageNumber
        || parsedPageNumber < 1;

    boolean invalidCommandObjects = storedCmd == null || currentCmd == null || !storedCmd.equals(currentCmd);
    return invalidCommandObjects || invalidParams;
  }
}
