/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
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
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Gets the file revisions for a given entry.
 *
 * @author jesper@users.berlios.de
 */
public class GetFileRevisionsController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final Map<String, Object> model = new HashMap<String, Object>();

    logger.debug("Finding revisions for [" + svnCommand.getPath() + "]");

    final long headRevision = getRepositoryService().getLatestRevision(repository);

    final List<Long> fileRevisions = new ArrayList<Long>();
    for (final SVNFileRevision fileRevision : getRepositoryService().getFileRevisions(
        repository, svnCommand.getPath(), headRevision)) {
      fileRevisions.add(fileRevision.getRevision());
    }
    logger.debug("Found revisions: " + fileRevisions);

    model.put("currentRevision", revision.getNumber());
    model.put("previousRevision", findPreviousRevision(fileRevisions, revision.getNumber()));
    model.put("nextRevision", findNextRevision(fileRevisions, revision.getNumber()));
    Collections.reverse(fileRevisions);
    model.put("allRevisions", fileRevisions);
    return new ModelAndView("ajax/fileRevisions", model);
  }

  protected Long findPreviousRevision(final List<Long> revisions, long targetRevision) {
    final int index = revisions.indexOf(targetRevision);
    if (index <= 0) {
      return null;
    }
    return revisions.get(index - 1);
  }

  protected Long findNextRevision(final List<Long> revisions, long targetRevision) {
    final int index = revisions.indexOf(targetRevision);
    if (index < 0 || index == revisions.size() - 1) {
      return null;
    }
    return revisions.get(index + 1);
  }

}
