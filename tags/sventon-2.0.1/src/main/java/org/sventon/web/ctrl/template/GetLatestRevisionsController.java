/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.web.ctrl.template;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.model.UserRepositoryContext;
import org.sventon.web.command.SVNBaseCommand;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gets the <i>N</i> latest revision details.
 *
 * @author jesper@sventon.org
 */
public final class GetLatestRevisionsController extends AbstractSVNTemplateController {

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final Map<String, Object> model = new HashMap<String, Object>();
    final List<SVNLogEntry> revisions = new ArrayList<SVNLogEntry>();
    final long revisionCount = userRepositoryContext.getLatestRevisionsDisplayCount();

    try {
      logger.debug("Getting [" + revisionCount + "] latest revisions");
      revisions.addAll(getRepositoryService().getRevisions(command.getName(), repository, -1, FIRST_REVISION, "/",
          revisionCount, false));
      logger.debug("Got [" + revisions.size() + "] revisions");
    } catch (SVNException svnex) {
      if (SVNErrorCode.FS_NO_SUCH_REVISION == svnex.getErrorMessage().getErrorCode()) {
        logger.info(svnex.getMessage());
        model.put("errorMessage", "There are no commits in this repository yet.");
      } else {
        logger.error(svnex.getMessage());
        model.put("errorMessage", svnex.getMessage());
      }
    }

    model.put("revisions", revisions);
    return new ModelAndView(getViewName(), model);
  }
}
