/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
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
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.web.model.RepositoryEntryTray;
import de.berlios.sventon.web.model.UserContext;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for the repository entry tray.
 *
 * @author jesper@users.berlios.de
 */
public class RepositoryEntryTrayController extends AbstractSVNTemplateController implements Controller {

  /**
   * Request parameter indicating entry should be added to tray.
   */
  public static final String PARAMETER_ADD = "add";

  /**
   * Request parameter indicating entry should be removed from tray.
   */
  public static final String PARAMETER_REMOVE = "remove";

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserContext userContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final String actionParameter = ServletRequestUtils.getRequiredStringParameter(request, "action");

    final RepositoryEntry entry = getRepositoryService().getEntryInfo(repository, svnCommand.getPath(),
        revision.getNumber());

    if (entry == null) {
      throw new IllegalArgumentException("Entry does not exist: " + svnCommand);
    }

    final RepositoryEntryTray entryTray = userContext.getRepositoryEntryTray();
    if (PARAMETER_ADD.equals(actionParameter)) {
      logger.debug("Adding entry to tray: " + entry.getFullEntryName());
      entryTray.add(entry);
    } else if (PARAMETER_REMOVE.equals(actionParameter)) {
      logger.debug("Removing entry from tray: " + entry.getFullEntryName());
      entryTray.remove(entry);
    } else {
      throw new UnsupportedOperationException(actionParameter);
    }
    return new ModelAndView("ajax/entryTray");
  }
}
