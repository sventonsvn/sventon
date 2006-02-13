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
package de.berlios.sventon.ctrl;

import de.berlios.sventon.command.SVNBaseCommand;
import de.berlios.sventon.svnsupport.SventonException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.SVNException;
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
public class SearchController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(SVNRepository repository, SVNBaseCommand svnCommand, SVNRevision revision,
                                   HttpServletRequest request, HttpServletResponse response, BindException exception) throws SventonException, SVNException {

    List<RepositoryEntry> entries = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);
    final String searchString = request.getParameter("searchString");
    final String startDir = request.getParameter("startDir");
    logger.debug("Searching index for [" + searchString + "] in directory [" + startDir + "]");

    if (searchString.toUpperCase().equals(searchString)) {
      logger.debug("Search string was in upper case only - performing CamelCase index search");
      String ccPattern = preparePattern(searchString);
      entries.addAll(getRevisionIndexer().findPattern(ccPattern, startDir));
    } else {
      entries.addAll(getRevisionIndexer().find(searchString, startDir));
    }

    logger.debug("Create model");
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("searchString", searchString);
    model.put("startDir", startDir);
    model.put("svndir", entries);
    model.put("isSearch", true);  // Indicates that path should be shown in browser view.
    return new ModelAndView("repobrowser", model);
  }

  /**
   * Prepares a regex search pattern based on given
   * search string.
   *
   * @param searchString The search string.
   * @return The regex pattern.
   */
  private String preparePattern(final String searchString) {
    StringBuffer sb = new StringBuffer(".*/");
    for (int i = 0; i < searchString.length(); i++) {
      sb.append("[");
      sb.append(searchString.charAt(i));
      sb.append("][a-z0-9]+");
    }
    sb.append(".*");
    return sb.toString();
  }

}
