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
import de.berlios.sventon.repository.RepositoryEntrySorter;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.FileExtensionList;
import de.berlios.sventon.web.model.UserContext;
import de.berlios.sventon.web.support.FileExtensionFilter;
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
 * RepoBrowserController.
 *
 * @author patrikfr@users.berlios.de
 * @author jesper@users.berlios.de
 */
public class RepoBrowserController extends ListDirectoryContentsController implements Controller {

  @SuppressWarnings("unchecked")
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserContext userContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final ModelAndView modelAndView = super.svnHandle(repository, svnCommand, revision, userContext, request,
        response, exception);

    final Map<String, Object> model = modelAndView.getModel();

    final String filterExtension = ServletRequestUtils.getStringParameter(request, "filterExtension", "all");
    logger.debug("filterExtension: " + filterExtension);

    List<RepositoryEntry> entries = (List<RepositoryEntry>) model.get("svndir");

    if (!"all".equals(filterExtension)) {
      final FileExtensionFilter fileExtensionFilter = new FileExtensionFilter(filterExtension);
      entries = fileExtensionFilter.filter(entries);
    }

    logger.debug("Sort params: " + userContext.getSortType().name() + ", " + userContext.getSortMode());
    new RepositoryEntrySorter(userContext.getSortType(), userContext.getSortMode()).sort(entries);

    logger.debug("Adding data to model");
    model.put("svndir", entries);
    model.put("locks", getRepositoryService().getLocks(repository, svnCommand.getPath()));
    model.put("existingExtensions", new FileExtensionList(entries).getExtensions());
    model.put("filterExtension", filterExtension);
    modelAndView.setViewName("repobrowser");
    return modelAndView;
  }
}
