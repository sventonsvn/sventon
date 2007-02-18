/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
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
import de.berlios.sventon.repository.cache.CamelCasePattern;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.UserContext;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Controller used when searching for file or directory entries in the repository.
 *
 * @author jesper@users.berlios.de
 */
public class SearchEntriesController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserContext userContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final String searchString = ServletRequestUtils.getStringParameter(request, "searchString");
    final String startDir = ServletRequestUtils.getStringParameter(request, "startDir");

    final Map<String, Object> model = new HashMap<String, Object>();

    logger.debug("Searching cache for [" + searchString + "] in directory [" + startDir + "]");
    final List<RepositoryEntry> entries = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);

    if (isAllUpperCase(searchString)) {
      logger.debug("Search string was in upper case only - performing CamelCase cache search");
      entries.addAll(getCache().findEntryByCamelCase(svnCommand.getName(), new CamelCasePattern(searchString), startDir));
    } else {
      entries.addAll(getCache().findEntry(svnCommand.getName(), searchString, startDir));
    }

    logger.debug("Sort params: " + userContext.getSortType().name() + ", " + userContext.getSortMode());
    new RepositoryEntrySorter(userContext.getSortType(), userContext.getSortMode()).sort(entries);

    logger.debug("Adding data to model");
    model.put("svndir", entries);
    model.put("searchString", searchString);
    model.put("locks", getRepositoryService().getLocks(repository, svnCommand.getPath()));
    model.put("startDir", startDir);
    model.put("isEntrySearch", true);  // Indicates that path should be shown in browser view.
    return new ModelAndView("searchEntriesResult", model);
  }

  private boolean isAllUpperCase(final String string) {
    return string.toUpperCase().equals(string);
  }
}
