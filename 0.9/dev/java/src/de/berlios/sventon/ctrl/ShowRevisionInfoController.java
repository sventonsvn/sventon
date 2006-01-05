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

import de.berlios.sventon.command.SVNBaseCommand;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller to fetch details for a specific revision.
 *
 * @author jesper@users.berlios.de
 */
public class ShowRevisionInfoController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
      HttpServletRequest request, HttpServletResponse response, BindException exception) throws SVNException {

    logger.debug("Fetching revision details");
    long revNumber = 0;

    try {
      revNumber = Long.parseLong(request.getParameter("rev"));
    } catch (NumberFormatException nfe) {
      exception.reject("log.command.invalidpath", "Invalid revision");
      return prepareExceptionModelAndView(exception, svnCommand);
    }

    logger.debug("Getting revision info details for revision: " + revNumber);
    SVNLogEntry logEntry = getRevisionInfo(repository, revNumber);

    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("revisionInfo", logEntry);
    model.put("properties", new HashMap()); // TODO: Replace with valid entry properties
    return new ModelAndView("showrevinfo", model);
  }
}
