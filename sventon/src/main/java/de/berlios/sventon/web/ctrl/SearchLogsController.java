/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.ctrl;

import de.berlios.sventon.repository.LogMessage;
import de.berlios.sventon.repository.LogMessageComparator;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller used when searching for log messages.
 *
 * @author jesper@users.berlios.de
 */
public final class SearchLogsController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final String searchString = ServletRequestUtils.getRequiredStringParameter(request, "searchString");
    final String startDir = ServletRequestUtils.getRequiredStringParameter(request, "startDir");

    logger.debug("Searching logMessages for: " + searchString);

    final List<LogMessage> logMessages = getCache().find(svnCommand.getName(), searchString);
    Collections.sort(logMessages, new LogMessageComparator(LogMessageComparator.DESCENDING));

    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("logMessages", logMessages);
    model.put("searchString", searchString);
    model.put("startDir", startDir);
    model.put("isLogSearch", true);  // Indicates that path should be shown in browser view.
    return new ModelAndView("searchLogsResult", model);
  }
}
