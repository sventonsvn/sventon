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
package org.sventon.web.ctrl.template;

import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.model.RepositoryEntry;
import org.sventon.model.UserRepositoryContext;
import org.sventon.util.RepositoryEntryKindFilter;
import org.sventon.web.command.SVNBaseCommand;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * ListFilesController.
 *
 * @author patrik@sventon.org
 * @author jesper@sventon.org
 */
public final class ListFilesController extends ListDirectoryContentsController {

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final ModelAndView modelAndView = super.svnHandle(repository, command, headRevision, userRepositoryContext, request,
        response, exception);

    final Map<String, Object> model = modelAndView.getModel();
    final List<RepositoryEntry> entries = (List<RepositoryEntry>) model.get("svndir");
    final RepositoryEntryKindFilter entryFilter = new RepositoryEntryKindFilter(RepositoryEntry.Kind.FILE);
    final int rowNumber = ServletRequestUtils.getRequiredIntParameter(request, "rowNumber");

    logger.debug("Adding data to model");
    model.put("svndir", entryFilter.filter(entries));
    model.put("rowNumber", rowNumber);
    modelAndView.setViewName(getViewName());
    return modelAndView;
  }
}
