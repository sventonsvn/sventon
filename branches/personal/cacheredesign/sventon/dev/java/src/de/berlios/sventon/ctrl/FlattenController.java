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
import de.berlios.sventon.repository.RepositoryEntry;
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
 * Controller used when flatting directory structure.
 * @author jesper@users.berlios.de
 */
public class FlattenController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand, final SVNRevision revision,
      final HttpServletRequest request, final HttpServletResponse response, final BindException exception)
      throws SventonException, SVNException {
    
    final List<RepositoryEntry> entries = Collections.checkedList(new ArrayList<RepositoryEntry>(), RepositoryEntry.class);

    // Make sure the path starts with a slash as that
    // is the path structure of the revision index.
    String fromPath = svnCommand.getPath();
    if (!fromPath.startsWith("/")) {
      logger.debug("Appending initial slash");
      fromPath = "/" + fromPath;
    }

    logger.debug("Flattening directories below: " + fromPath);
    entries.addAll(getRevisionIndexer().findDirectories(fromPath));
    logger.debug(entries.size() + " entries found");

    logger.debug("Create model");
    final Map<String, Object> model = new HashMap<String, Object>();
    model.put("svndir", entries);
    model.put("isFlatten", true);  // Indicates that path should be shown in browser view.
    return new ModelAndView("repobrowser", model);
  }
}
