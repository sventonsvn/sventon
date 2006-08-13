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

import de.berlios.sventon.web.command.DiffCommand;
import de.berlios.sventon.web.command.SVNBaseCommand;
import de.berlios.sventon.diff.DiffException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.RequestUtils;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The DiffPreviousController generates a diff between a given entry
 * and its previous entry in history.
 *
 * @author jesper@users.berlios.de
 */
public class DiffPreviousController extends DiffController {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final HttpServletRequest request,
                                   final HttpServletResponse response, final BindException exception) throws Exception {

    final long commitRev = RequestUtils.getLongParameter(request, "commitrev");
    logger.debug("Diffing file contents for: " + svnCommand);
    logger.debug("committed-rev: " + commitRev);
    final Map<String, Object> model = new HashMap<String, Object>();

    try {
      //TODO: Solve this issue in a better way?
      if (SVNNodeKind.NONE == repository.checkPath(svnCommand.getPath(), commitRev)) {
        throw new DiffException("Entry has no history in current branch");
      }
      //noinspection unchecked
      final List<SVNFileRevision> revisions = (List) repository.getFileRevisions(svnCommand.getPath(), null, 0, commitRev);
      final DiffCommand diffCommand = new DiffCommand(revisions);
      model.put("diffCommand", diffCommand);
      logger.debug("Using: " + diffCommand);

      model.putAll(diffInternal(repository, diffCommand,
          getConfiguration().getInstanceConfiguration(svnCommand.getName())));

    } catch (DiffException dex) {
      model.put("diffException", dex.getMessage());
    }

    return new ModelAndView("diff", model);
  }

}
