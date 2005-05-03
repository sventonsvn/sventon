package de.berlios.sventon.ctrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

public class ShowLogController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, long revision,
      HttpServletRequest request, HttpServletResponse response) throws SVNException {

    String[] targetPaths = new String[] { svnCommand.getPath() };

    List<SVNLogEntry> logEntries = null;
    SVNNodeKind nodeKind = null;

    logger.debug("Assembling logs data");
    logEntries = (List<SVNLogEntry>) repository.log(targetPaths, new ArrayList(), 0, repository.getLatestRevision(),
        true, false);
    nodeKind = repository.checkPath(svnCommand.getPath(), revision);

    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();

    Collections.reverse(logEntries);
    model.put("logEntries", logEntries);
    model.put("isFile", nodeKind == SVNNodeKind.FILE);

    return new ModelAndView("showlog", model);
  }
}
