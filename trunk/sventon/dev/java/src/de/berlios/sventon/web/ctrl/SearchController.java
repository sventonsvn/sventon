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

import de.berlios.sventon.SventonException;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.repository.LogMessage;
import de.berlios.sventon.repository.RepositoryEntry;
import de.berlios.sventon.repository.LogMessageComparator;
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
public class SearchController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final HttpServletRequest request,
                                   final HttpServletResponse response, final BindException exception) throws Exception {

    final String searchString = RequestUtils.getStringParameter(request, "searchString");
    final String startDir = RequestUtils.getStringParameter(request, "startDir");
    final String searchMode = RequestUtils.getStringParameter(request, "searchMode", "entries");

    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("searchString", searchString);
    model.put("startDir", startDir);
    model.put("searchMode", searchMode);
    model.put("isSearch", true);  // Indicates that path should be shown in browser view.

    logger.debug("Search mode is: " + searchMode);
    if ("entries".equals(searchMode)) {
      logger.debug("Searching cache for [" + searchString + "] in directory [" + startDir + "]");

      final List<RepositoryEntry> entries = Collections.checkedList(new ArrayList<RepositoryEntry>(),
          RepositoryEntry.class);

      if (searchString.toUpperCase().equals(searchString)) {
        logger.debug("Search string was in upper case only - performing CamelCase cache search");
        String ccPattern = preparePattern(searchString);
        //entries.addAll(getCacheService().findPattern(ccPattern, startDir));
      } else {
        entries.addAll(getCacheService().findEntry(searchString, startDir));
      }
      model.put("svndir", entries);
    } else if ("logMessages".equals(searchMode)) {
      logger.debug("Searchin logMessages for: " + searchString);

      final List<LogMessage> logMessages = Collections.checkedList(new ArrayList<LogMessage>(),
          LogMessage.class);
      logMessages.addAll(getCacheService().find(searchString));
      Collections.sort(logMessages, new LogMessageComparator(LogMessageComparator.DESCENDING));
      model.put("logMessages", logMessages);
    } else {
      throw new SventonException("Illegal searchMode: " + searchMode);
    }
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
    final StringBuffer sb = new StringBuffer(".*/");
    for (int i = 0; i < searchString.length(); i++) {
      sb.append("[");
      sb.append(searchString.charAt(i));
      sb.append("][a-z0-9]+");
    }
    sb.append(".*");
    return sb.toString();
  }

}
