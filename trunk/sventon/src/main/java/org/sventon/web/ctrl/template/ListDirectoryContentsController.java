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
import org.springframework.web.servlet.ModelAndView;
import org.sventon.model.RepositoryEntry;
import org.sventon.model.UserRepositoryContext;
import org.sventon.web.command.SVNBaseCommand;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.io.SVNRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ListDirectoryContentsController.
 * Controller that lists the contents of given repository path at given revision.
 * The resulting model will include:
 * <ul>
 * <li><code>svndir</code> - The list of <code>SVNDirEntry</code> instances</li>
 * <li><code>properties</code> - The path's SVN properties</li>
 * </ul>
 * Note: Sub classes must specify <code>viewName</code> property.
 *
 * @author jesper@sventon.org
 */
public class ListDirectoryContentsController extends AbstractSVNTemplateController {

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    logger.debug("Getting directory contents for: " + svnCommand.getPath());
    final SVNProperties properties = new SVNProperties();
    final List<RepositoryEntry> entries = getRepositoryService().list(
        repository, svnCommand.getPath(), svnCommand.getRevisionNumber(), properties);

    final Map<String, Object> model = new HashMap<String, Object>();
    logger.debug(properties);
    model.put("svndir", entries);
    model.put("properties", properties);
    final ModelAndView modelAndView = new ModelAndView();
    modelAndView.addAllObjects(model);
    return modelAndView;
  }
}
