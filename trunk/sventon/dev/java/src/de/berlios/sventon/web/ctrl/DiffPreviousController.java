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
import de.berlios.sventon.web.model.UserContext;
import de.berlios.sventon.web.model.SideBySideDiffRow;
import de.berlios.sventon.diff.IdenticalFilesException;
import de.berlios.sventon.diff.IllegalFileFormatException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
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
 * The DiffPreviousController generates a Side-by-side diff between a given entry
 * and its previous entry in history.
 *
 * @author jesper@users.berlios.de
 */
public class DiffPreviousController extends AbstractSVNTemplateController implements Controller {

  /**
   * {@inheritDoc}
   */
  protected ModelAndView svnHandle(final SVNRepository repository, final SVNBaseCommand svnCommand,
                                   final SVNRevision revision, final UserContext userContext,
                                   final HttpServletRequest request, final HttpServletResponse response,
                                   final BindException exception) throws Exception {

    final long commitRev = ServletRequestUtils.getLongParameter(request, "commitrev");
    logger.debug("Diffing file (previous): " + svnCommand);
    logger.debug("committed-rev: " + commitRev);
    final Map<String, Object> model = new HashMap<String, Object>();

    //TODO: Solve this issue in a better way?
    if (SVNNodeKind.NONE == getRepositoryService().getNodeKind(repository, svnCommand.getPath(), commitRev)) {
      model.put("isMissingHistory", true);
    } else {
      final List<SVNFileRevision> revisions =
          getRepositoryService().getFileRevisions(repository, svnCommand.getPath(), commitRev);

      final DiffCommand diffCommand = new DiffCommand(revisions);
      model.put("diffCommand", diffCommand);
      logger.debug("Using: " + diffCommand);

      try {
        final List<SideBySideDiffRow> diffResult = getRepositoryService().diffSideBySide(
            repository, diffCommand, "UTF-8", getConfiguration().getInstanceConfiguration(svnCommand.getName()));

        model.put("diffResult", diffResult);
        model.put("isIdentical", false);
        model.put("isBinary", false);
      } catch (final IdenticalFilesException ife) {
        logger.debug("Files are identical");
        model.put("isIdentical", true);
      } catch (final IllegalFileFormatException iffe) {
        logger.info("Binary file(s) detected", iffe);
        model.put("isBinary", true);  // Indicates that one or both files are in binary format.
      }
    }

    return new ModelAndView("diff", model);
  }

}
