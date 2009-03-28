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
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.sventon.model.CamelCasePattern;
import org.sventon.model.RepositoryEntry;
import org.sventon.model.UserRepositoryContext;
import org.sventon.util.RepositoryEntrySorter;
import org.sventon.web.command.BaseCommand;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Controller used when searching for file or directory entries in the repository.
 *
 * @author jesper@sventon.org
 */
public final class SearchEntriesController extends AbstractTemplateController {

  public static final String SEARCH_STRING_PARAMETER = "searchString";

  public static final String START_DIR_PARAMETER = "startDir";

  private boolean includeAuthorInSearch;

  /**
   * Available search types.
   */
  public enum SearchType {
    TEXT,
    CAMELCASE
  }

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final BaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final String searchString = ServletRequestUtils.getRequiredStringParameter(request, SEARCH_STRING_PARAMETER);
    final String startDir = ServletRequestUtils.getRequiredStringParameter(request, START_DIR_PARAMETER);

    final Map<String, Object> model = new HashMap<String, Object>();

    logger.debug("Searching cache for [" + searchString + "] in directory [" + startDir + "]");
    final List<RepositoryEntry> entries = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);

    if (CamelCasePattern.isValid(searchString)) {
      logger.debug("Search string was in upper case only - performing CamelCase cache search");
      entries.addAll(getCache().findEntriesByCamelCase(command.getName(), new CamelCasePattern(searchString), startDir));
      model.put("searchType", SearchType.CAMELCASE);
    } else {
      entries.addAll(getCache().findEntries(command.getName(), searchString, startDir, includeAuthorInSearch));
      model.put("searchType", SearchType.TEXT);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("Sort params: " + userRepositoryContext.getSortType().name() + ", "
          + userRepositoryContext.getSortMode());
    }

    new RepositoryEntrySorter(userRepositoryContext.getSortType(), userRepositoryContext.getSortMode()).sort(entries);

    model.put("svndir", entries);
    model.put("searchString", searchString);
    model.put("locks", getRepositoryService().getLocks(repository, command.getPath()));
    model.put("startDir", startDir);
    model.put("isEntrySearch", true);  // Indicates that path should be shown in browser view.
    return new ModelAndView(getViewName(), model);
  }

  /**
   * @param includeAuthorInSearch True if the entry's author string should be included in the search.
   */
  public void setIncludeAuthorInSearch(final boolean includeAuthorInSearch) {
    this.includeAuthorInSearch = includeAuthorInSearch;
  }

}
