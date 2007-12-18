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

import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.repository.RepositoryEntryKindFilter;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.UserRepositoryContext;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * ListFilesController.
 *
 * @author patrikfr@users.berlios.de
 * @author jesper@users.berlios.de
 */
public final class ListFilesController extends ListDirectoryContentsController implements Controller {

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final ModelAndView modelAndView = super.svnHandle(repository, svnCommand, revision, userRepositoryContext, request,
        response, exception);

    final Map<String, Object> model = modelAndView.getModel();
    final List<RepositoryEntry> entries = (List<RepositoryEntry>) model.get("svndir");
    final RepositoryEntryKindFilter entryFilter = new RepositoryEntryKindFilter(RepositoryEntry.Kind.file);
    final int rowNumber = ServletRequestUtils.getRequiredIntParameter(request, "rowNumber");

    logger.debug("Adding data to model");
    model.put("svndir", entryFilter.filter(entries));
    model.put("rowNumber", rowNumber);
    modelAndView.setViewName("ajax/listFiles");
    return modelAndView;
  }
}
