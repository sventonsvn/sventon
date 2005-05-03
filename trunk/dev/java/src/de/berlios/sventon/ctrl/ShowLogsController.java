package nu.snart.os.servletsvn.ctrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.io.SVNException;
import org.tmatesoft.svn.core.io.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

public class ShowLogsController extends SVNServletBaseController implements Controller {
  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
      IOException {

    String path = RequestUtils.getStringParameter(request, "path", "");

    // log messages String[] targetPaths = new String[] {""};
    
    String[] targetPaths = new String[] {path};
    
    List<SVNLogEntry> logEntries = null;
    String latestRevision = null;

    try {

      
      logger.debug("Getting SVN repository");
      SVNRepository repository = SVNRepositoryFactory.create(configuration.getLocation());
      
      logger.debug("Assembling logs data");
      logEntries = (List<SVNLogEntry>) repository.log(targetPaths, new ArrayList(), 0, repository.getLatestRevision(), true, true);
      latestRevision = Long.toString(repository.getLatestRevision());
      
    } catch (SVNException e) {
      logger.error("SVN Exception", e);
    }

    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();

    model.put("url", configuration.getUrl());
    model.put("latestRevision", latestRevision);
    model.put("path", path);
    Collections.reverse(logEntries);
    model.put("logEntries", logEntries);

    return new ModelAndView("logs", "model", model);
  }
}
