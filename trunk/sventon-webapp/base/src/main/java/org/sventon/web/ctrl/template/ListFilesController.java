/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
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
import org.sventon.SVNConnection;
import org.sventon.model.DirEntry;
import org.sventon.model.DirEntryKindFilter;
import org.sventon.web.UserRepositoryContext;
import org.sventon.web.command.BaseCommand;

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

  @Override
  protected ModelAndView svnHandle(final SVNConnection connection, final BaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final ModelAndView modelAndView = super.svnHandle(connection, command, headRevision, userRepositoryContext, request,
        response, exception);

    final Map<String, Object> model = modelAndView.getModel();
    final List<DirEntry> entries = (List<DirEntry>) model.get("dirEntries");
    final DirEntryKindFilter entryFilter = new DirEntryKindFilter(DirEntry.Kind.FILE);
    final int rowNumber = ServletRequestUtils.getRequiredIntParameter(request, "rowNumber");

    logger.debug("Adding data to model");
    model.put("dirEntries", entryFilter.filter(entries));
    model.put("rowNumber", rowNumber);
    modelAndView.setViewName(getViewName());
    return modelAndView;
  }
}
