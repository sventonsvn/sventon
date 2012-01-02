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
import org.springframework.web.servlet.ModelAndView;
import org.sventon.SVNConnection;
import org.sventon.model.DirList;
import org.sventon.web.UserRepositoryContext;
import org.sventon.web.command.BaseCommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * ListDirectoryContentsController.
 * Controller that lists the contents of given repository path at given revision.
 * The resulting model will include:
 * <ul>
 * <li><code>dirEntries</code> - The list of <code>DirEntry</code> instances</li>
 * <li><code>properties</code> - The path's SVN properties</li>
 * </ul>
 * Note: Sub classes must specify <code>viewName</code> property.
 *
 * @author jesper@sventon.org
 */
public class ListDirectoryContentsController extends AbstractTemplateController {

  @Override
  protected ModelAndView svnHandle(final SVNConnection connection, final BaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    logger.debug("Getting directory contents for: " + command.getPath());
    final String path = command.getPathWithTrailingSlash();
    final long revision = command.getRevisionNumber();
    final DirList dirList = getRepositoryService().list(connection, path, revision);
    logger.debug("Directory entries in " + path + ": " + dirList.getEntriesCount());
    logger.debug("Properties for " + path + ": " + dirList.getPropertiesCount());

    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("dirEntries", dirList.getEntries());
    model.put("properties", dirList.getProperties());
    final ModelAndView modelAndView = new ModelAndView();
    modelAndView.addAllObjects(model);
    return modelAndView;
  }
}
