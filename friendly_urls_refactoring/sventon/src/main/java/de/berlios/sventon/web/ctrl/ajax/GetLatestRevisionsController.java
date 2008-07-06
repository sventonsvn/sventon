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
package de.berlios.sventon.web.ctrl.ajax;

import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.ctrl.AbstractSVNTemplateController;
import de.berlios.sventon.web.model.UserRepositoryContext;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gets the <i>N</i> latest revision details.
 *
 * @author jesper@users.berlios.de
 */
public final class GetLatestRevisionsController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    long revisionCount = userRepositoryContext.getLatestRevisionsDisplayCount();
    logger.debug("Getting [" + revisionCount + "] latest revisions");
    final List<SVNLogEntry> revisions = getRepositoryService().getRevisions(
        svnCommand.getName(), repository, -1, FIRST_REVISION, "/", revisionCount);
    logger.debug("Got [" + revisions.size() + "] revisions");

    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("revisions", revisions);

    return new ModelAndView("ajax/latestRevisions", model);
  }
}
