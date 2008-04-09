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
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Gets the file revision history for a given entry.
 *
 * @author jesper@users.berlios.de
 */
public final class GetFileHistoryController extends AbstractSVNTemplateController implements Controller {

  /**
   * Request parameter identifying the arcived entry to display.
   */
  protected static final String ARCHIVED_ENTRY = "archivedEntry";

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final Map<String, Object> model = new HashMap<String, Object>();

    logger.debug("Finding revisions for [" + svnCommand.getPath() + "]");

    final String archivedEntry = ServletRequestUtils.getStringParameter(request, ARCHIVED_ENTRY, null);

    final List<SVNFileRevision> fileRevisions = getRepositoryService().getFileRevisions(
        repository, svnCommand.getPath(), svnCommand.getRevisionNumber());

    if (logger.isDebugEnabled()) {
      final List<Long> fileRevisionNumbers = new ArrayList<Long>();
      for (final SVNFileRevision fileRevision : fileRevisions) {
        fileRevisionNumbers.add(fileRevision.getRevision());
      }
      logger.debug("Found revisions: " + fileRevisionNumbers);
    }

    Collections.reverse(fileRevisions);
    model.put("currentRevision", svnCommand.getRevisionNumber());
    model.put("fileRevisions", fileRevisions);
    if (archivedEntry != null) {
      model.put(ARCHIVED_ENTRY, archivedEntry);
    }
    return new ModelAndView("ajax/fileHistory", model);
  }
}
