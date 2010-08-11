/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
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
import org.sventon.SVNConnection;
import org.sventon.appl.RepositoryConfiguration;
import org.sventon.model.*;
import org.sventon.web.command.BaseCommand;
import org.tmatesoft.svn.core.SVNException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for the repository entry tray.
 *
 * @author jesper@sventon.org
 */
public final class DirEntryTrayController extends AbstractTemplateController {

  /**
   * Request parameter indicating entry should be added to tray.
   */
  public static final String PARAMETER_ADD = "add";

  /**
   * Request parameter indicating entry should be removed from tray.
   */
  public static final String PARAMETER_REMOVE = "remove";

  @Override
  protected ModelAndView svnHandle(final SVNConnection connection, final BaseCommand command,
                                   final long headRevision, final UserRepositoryContext userRepositoryContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    assertEntryTrayEnabled(command.getName());
    final String actionParameter = ServletRequestUtils.getRequiredStringParameter(request, "action");
    final long pegRevision = command.hasPegRevision() ? command.getPegRevision() : command.getRevisionNumber();

    final ModelAndView modelAndView = new ModelAndView(getViewName());

    final DirEntry entry;
    try {
      entry = getRepositoryService().getEntryInfo(connection, command.getPath(), pegRevision);
    } catch (SVNException e) {
      return modelAndView;
    }

    final DirEntryTray entryTray = userRepositoryContext.getDirEntryTray();

    if (PARAMETER_ADD.equals(actionParameter)) {
      logger.debug("Adding entry to tray: " + entry.getFullEntryName());
      entryTray.add(new PeggedRepositoryEntry(entry, pegRevision));
    } else if (PARAMETER_REMOVE.equals(actionParameter)) {
      logger.debug("Removing entry from tray: " + entry.getFullEntryName());
      entryTray.remove(new PeggedRepositoryEntry(entry, pegRevision));
    } else {
      throw new UnsupportedOperationException(actionParameter);
    }
    return modelAndView;
  }

  void assertEntryTrayEnabled(final RepositoryName name) {
    final RepositoryConfiguration configuration = getRepositoryConfiguration(name);
    if (!configuration.isEntryTrayEnabled()) {
      throw new UnsupportedOperationException("The EntryTray is disabled in the config file for repository " + name);
    }
  }
}
