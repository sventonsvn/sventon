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
import org.sventon.cache.CamelCasePattern;
import org.sventon.model.RepositoryEntry;
import org.sventon.model.UserRepositoryContext;
import org.sventon.util.RepositoryEntrySorter;
import org.sventon.web.command.SVNBaseCommand;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Controller used when searching for file or directory entries in the repository.
 *
 * @author jesper@sventon.org
 */
public final class SearchEntriesController extends AbstractSVNTemplateController {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final String searchString = ServletRequestUtils.getRequiredStringParameter(request, "searchString");
    final String startDir = ServletRequestUtils.getRequiredStringParameter(request, "startDir");

    final Map<String, Object> model = new HashMap<String, Object>();

    logger.debug("Searching cache for [" + searchString + "] in directory [" + startDir + "]");
    final List<RepositoryEntry> entries = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);

    if (isAllUpperCase(searchString)) {
      logger.debug("Search string was in upper case only - performing CamelCase cache search");
      entries.addAll(getCache().findEntryByCamelCase(svnCommand.getName(), new CamelCasePattern(searchString), startDir));
    } else {
      entries.addAll(getCache().findEntry(svnCommand.getName(), searchString, startDir));
    }

    if (logger.isDebugEnabled()) {
      logger.debug("Sort params: " + userRepositoryContext.getSortType().name() + ", "
          + userRepositoryContext.getSortMode());
    }

    new RepositoryEntrySorter(userRepositoryContext.getSortType(), userRepositoryContext.getSortMode()).sort(entries);

    logger.debug("Adding data to model");
    model.put("svndir", entries);
    model.put("searchString", searchString);
    model.put("locks", getRepositoryService().getLocks(repository, svnCommand.getPath()));
    model.put("startDir", startDir);
    model.put("isEntrySearch", true);  // Indicates that path should be shown in browser view.
    return new ModelAndView(getViewName(), model);
  }

  /**
   * Checks if all characters in given string is in upper case.
   *
   * @param str String.
   * @return True if all is uppercase.
   */
  private boolean isAllUpperCase(final String str) {
    return str.toUpperCase().equals(str);
  }
}
