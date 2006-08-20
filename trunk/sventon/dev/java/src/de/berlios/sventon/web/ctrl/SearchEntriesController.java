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
import org.springframework.validation.BindException;
import org.springframework.web.bind.RequestUtils;
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
                                   final SVNRevision revision, final HttpServletRequest request,
                                   final HttpServletResponse response, final BindException exception) throws Exception {

    final String searchString = RequestUtils.getStringParameter(request, "searchString");
    final String startDir = RequestUtils.getStringParameter(request, "startDir");

    final Map<String, Object> model = new HashMap<String, Object>();

    logger.debug("Searching cache for [" + searchString + "] in directory [" + startDir + "]");
    final List<RepositoryEntry> entries = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);

    if (isAllUpperCase(searchString)) {
      logger.debug("Search string was in upper case only - performing CamelCase cache search");
      entries.addAll(getCache().findEntryByCamelCase(svnCommand.getName(), new CamelCasePattern(searchString), startDir));
    } else {
      entries.addAll(getCache().findEntry(svnCommand.getName(), searchString, startDir));
    }

    new RepositoryEntrySorter(svnCommand.getSortType(), svnCommand.getSortMode()).sort(entries);
    model.put("svndir", entries);
    model.put("searchString", searchString);
    model.put("startDir", startDir);
    model.put("isEntrySearch", true);  // Indicates that path should be shown in browser view.
    return new ModelAndView("repobrowser", model);
  }

  private boolean isAllUpperCase(final String string) {
    return string.toUpperCase().equals(string);
  }
}
