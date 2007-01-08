/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.UserContext;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
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
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserContext userContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    logger.debug("Create model");
    final Map<String, Object> model = new HashMap<String, Object>();

    final long revNumber;
    try {
      revNumber = ServletRequestUtils.getLongParameter(request, "revision");
    } catch (final ServletRequestBindingException ex) {
      exception.reject("goto.command.invalidpath", "Invalid revision");
      return prepareExceptionModelAndView(exception, svnCommand);
    }

    logger.debug("Getting revision info details for revision: " + revNumber);
    model.put("revisionInfo", getRepositoryService().getRevision(repository, revNumber));
    return new ModelAndView("showrevinfo", model);
  }

}
